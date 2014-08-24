package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.client.events.ConnectedEvent;
import org.fryingpanjoe.bigbattle.client.events.DisconnectedEvent;
import org.fryingpanjoe.bigbattle.common.networking.Channel;
import org.fryingpanjoe.bigbattle.common.networking.Packet;
import org.fryingpanjoe.bigbattle.common.networking.Protocol;

import com.google.common.eventbus.EventBus;

public class ClientNetworkManager {

  private static final Logger LOG = Logger.getLogger(ClientNetworkManager.class.getName());

  private final EventBus eventBus;
  private ClientChannel channel;

  public ClientNetworkManager(final EventBus eventBus) {
    this.eventBus = eventBus;
    this.channel = null;
  }

  public void connect(final String host, final int port) throws IOException {
    disconnect();
    LOG.info(String.format("Connecting to server %s:%d", host, port));
    final InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(host), port);
    final DatagramChannel socket = DatagramChannel.open();
    socket.configureBlocking(false);
    socket.connect(address);
    this.channel = new ClientChannel(new Channel(socket, address), socket, address);
    this.eventBus.post(new ConnectedEvent());
  }

  public void disconnect() {
    if (this.channel != null) {
      LOG.info("Disconnecting from server");
      this.eventBus.post(new DisconnectedEvent());
      this.channel.close();
      this.channel = null;
    }
  }

  public void receivePacketFromServer() {
    if (this.channel != null) {
      try {
        final Packet packet = this.channel.receivePacket();
        if (packet != null) {
          onPacketReceivedFromServer(packet);
        }
      } catch (final IOException e) {
        LOG.warning("Failed to receive packet from server: " + e.getMessage());
        disconnect();
      }
    }
  }

  public void sendPacketToServer(final ByteBuffer packet) {
    if (this.channel != null) {
      try {
        this.channel.sendPacket(packet);
      } catch (final IOException e) {
        LOG.warning("Failed to send packet to server: " + e.getMessage());
        disconnect();
      }
    }
  }

  private void onPacketReceivedFromServer(final Packet packet) {
    final ByteBuffer data = ByteBuffer.wrap(packet.getData());
    final Protocol.PacketType packetType = Protocol.readPacketHeader(data);
    switch (packetType) {
      case EnterGameEvent:
        this.eventBus.post(Protocol.readEnterGameEvent(data));
        break;

      case EntityNoticedEvent:
        this.eventBus.post(Protocol.readEntityNoticedEvent(data));
        break;

      case EntityLostEvent:
        this.eventBus.post(Protocol.readEntityLostEvent(data));
        break;

      default:
        LOG.info("Ignoring packet: " + packetType);
    }
  }
}
