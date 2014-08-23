package org.fryingpanjoe.bigbattle.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.common.networking.Channel;
import org.fryingpanjoe.bigbattle.common.networking.Packet;
import org.fryingpanjoe.bigbattle.server.config.ServerConfig;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class Main {

  private static final Logger LOG = Logger.getLogger(Main.class.getName());

  public static void main(final String[] argv) throws Exception {
    LOG.info("Starting big-battle-server");

    final ServerConfig config = new ServerConfig();

    final EventBus eventBus = new EventBus();
    final ServerEntityManager serverEntityManager = new ServerEntityManager();
    final ServerAreaManager serverAreaManager = new ServerAreaManager();
    final ServerNetworkManager serverNetworkManager = new ServerNetworkManager(eventBus);
    serverNetworkManager.bind(config.getBindAddress(), config.getBindPort());

    int frame = 0;

    final UpdateTimer serverFrameTimer = UpdateTimer.fromMaxFps(config.getMaxFps());
    while (true) {
      serverNetworkManager.checkTimeouts();
      serverNetworkManager.receivePacketsFromClients();
      final long timeUntilUpdate = serverFrameTimer.getTimeUntilUpdate();
      if (timeUntilUpdate > 0) {
        //Thread.sleep(timeUntilUpdate);
        Thread.sleep(1);
        continue;
      }
      for (final Entity entity : serverEntityManager)
      // compute delta
      /*final ByteBuffer deltaPacket = Channel.createPacketBuffer();
      deltaPacket.putInt(frame);
      deltaPacket.flip();
      // send data to clients
      serverNetworkManager.sendPacketTo(clientId, packet);
      final Iterator<Channel> clientChannelIterator = clientChannels.values().iterator();
      while (clientChannelIterator.hasNext()) {
        try {
          clientChannelIterator.next().sendPacket(deltaPacket);
        } catch (final IOException e) {
          e.printStackTrace();
          LOG.info("Client disconnected");
          clientChannelIterator.remove();
        }
      }
      */
    }
  }
}
