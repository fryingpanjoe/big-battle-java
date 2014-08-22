package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.common.networking.Channel;
import org.fryingpanjoe.bigbattle.common.networking.Packet;

public class ClientChannel {

  private static final Logger LOG = Logger.getLogger(ClientChannel.class.getName());

  private final Channel channel;
  private final DatagramChannel socket;
  private final InetSocketAddress address;

  public ClientChannel(final Channel channel,
                       final DatagramChannel socket,
                       final InetSocketAddress address) {
    this.channel = channel;
    this.socket = socket;
    this.address = address;
  }

  public void close() {
    if (this.socket.isOpen()) {
      try {
        this.socket.close();
      } catch (final IOException e) {
        // ignore?
      }
    }
  }

  public Packet receivePacket() throws IOException {
    final ByteBuffer receivedData = Channel.createPacketBuffer();
    final SocketAddress from = this.socket.receive(receivedData);
    if (from != null) {
      /*if (!from.equals(this.address)) {
        LOG.warning("Received data did not come from server");
      }*/
      receivedData.flip();
      return this.channel.onDataReceived(receivedData);
    } else {
      return null;
    }
  }

  public void sendPacket(final ByteBuffer data) throws IOException {
    data.flip();
    this.channel.sendPacket(data);
  }
}
