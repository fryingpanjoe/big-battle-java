package org.fryingpanjoe.bigbattle.server;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.common.events.EnterGameEvent;
import org.fryingpanjoe.bigbattle.common.events.EntityLostEvent;
import org.fryingpanjoe.bigbattle.common.events.EntityNoticedEvent;
import org.fryingpanjoe.bigbattle.common.networking.Channel;
import org.fryingpanjoe.bigbattle.common.networking.Protocol;
import org.fryingpanjoe.bigbattle.server.config.ServerConfig;
import org.fryingpanjoe.bigbattle.server.events.ClientConnectedEvent;
import org.fryingpanjoe.bigbattle.server.events.ClientDisconnectedEvent;
import org.fryingpanjoe.bigbattle.server.events.ReceivedPacketFromClientEvent;
import org.fryingpanjoe.bigbattle.server.game.ServerEntity;
import org.fryingpanjoe.bigbattle.server.game.ServerPlayer;
import org.lwjgl.Sys;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class Main {

  private static final Logger LOG = Logger.getLogger(Main.class.getName());

  public static void main(final String[] argv) throws Exception {
    LOG.info("Starting big-battle-server");

    final ServerConfig config = new ServerConfig();

    final ServerEntityManager entityManager = new ServerEntityManager();
    final ServerPlayerManager playerManager = new ServerPlayerManager();
    final ServerNoticeManager noticeManager = new ServerNoticeManager(entityManager);
    final ServerSpawner spawner = new ServerSpawner(entityManager, playerManager, noticeManager);

    final EventBus eventBus = new EventBus();
    final ServerNetworkManager networkManager = new ServerNetworkManager(eventBus);
    final Object eventHandler = new Object() {

      @Subscribe
      public void onReceivedPacketFromClientEvent(final ReceivedPacketFromClientEvent event) {
        final ByteBuffer data = ByteBuffer.wrap(event.packet.getData());
        final Protocol.PacketType packetType = Protocol.readPacketHeader(data);
        switch (packetType) {
          case PlayerInput: {
            final ServerPlayer player = playerManager.getPlayerByClientId(event.clientId);
            player.setPlayerInput(Protocol.readPlayerInput(data));
          } break;

          case EntityDelta: {
            final ServerPlayer player = playerManager.getPlayerByClientId(event.clientId);
            if (player != null) {
              Protocol.readEntityDelta(data, player.getServerEntity().getEntity());
            }
          } break;

          case Goodbye: {
            networkManager.disconnectClient(event.clientId);
          } break;

          default:
            break;
        }
      }

      @Subscribe
      public void onClientConnectedEvent(final ClientConnectedEvent event) {
        LOG.info("Client " + event.clientId + " connected, sending enter game");
        // TODO find valid spawn location
        final float x = 100.f;
        final float y = 100.f;
        final ServerPlayer player = spawner.spawnClientPlayer(event.clientId, x, y);
        final ByteBuffer packet = Channel.createPacketBuffer();
        Protocol.writePacketHeader(packet, Protocol.PacketType.EnterGameEvent);
        Protocol.writeEnterGameEvent(
          packet, new EnterGameEvent(event.clientId, player.getServerEntity().getEntity().getId()));
        packet.flip();
        networkManager.sendPacketTo(event.clientId, packet);
      }

      @Subscribe
      public void onClientDisconnectedEvent(final ClientDisconnectedEvent event) {
        spawner.killClientPlayer(event.clientId);
      }
    };
    eventBus.register(eventHandler);

    networkManager.bind(config.getBindAddress(), config.getBindPort());

    long lastUpdatedAt = Sys.getTime();

    final UpdateTimer serverFrameTimer = UpdateTimer.fromMaxFps(config.getMaxFps());

    while (true) {
      final long timeUntilUpdate = serverFrameTimer.getTimeUntilUpdate();
      if (timeUntilUpdate > 0) {
        Thread.sleep(timeUntilUpdate);
        //Thread.sleep(1);
        continue;
      }

      // get updates from client
      networkManager.receivePacketsFromClients();
      networkManager.checkTimeouts();
      networkManager.removeDisconnectedClients();

      // update world
      final long now = Sys.getTime();
      final long deltaTime = now - lastUpdatedAt;
      lastUpdatedAt = now;
      final float dt = (float) deltaTime / 1000.f;
      playerManager.updatePlayerInput();
      entityManager.updatePositions(dt);
      noticeManager.updateNotices();

      // send updates to clients
      for (final ServerPlayer player : playerManager.getPlayers().values()) {
        final int clientId = player.getPlayer().getClientId();
        // update noticed entities
        for (final int entityId : player.getNotice().getNoticed()) {
          final ServerEntity entity = entityManager.getEntity(entityId);
          final ByteBuffer packet = Channel.createPacketBuffer();
          Protocol.writePacketHeader(packet, Protocol.PacketType.EntityNoticedEvent);
          Protocol.writeEntityNoticedEvent(packet, new EntityNoticedEvent(entity.getEntity()));
          packet.flip();
          networkManager.sendPacketTo(clientId, packet);
        }
        // update lost entities
        for (final int entityId : player.getNotice().getLost()) {
          final ByteBuffer packet = Channel.createPacketBuffer();
          Protocol.writePacketHeader(packet, Protocol.PacketType.EntityLostEvent);
          Protocol.writeEntityLostEvent(packet, new EntityLostEvent(entityId));
          packet.flip();
          networkManager.sendPacketTo(clientId, packet);
        }
        player.getNotice().clearLost();
      }
    }
  }
}
