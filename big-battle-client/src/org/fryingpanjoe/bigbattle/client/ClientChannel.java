package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import org.fryingpanjoe.bigbattle.common.networking.Channel;
import org.fryingpanjoe.bigbattle.common.networking.Packet;

public class ClientChannel {

  private final Channel channel;
  private final DatagramChannel socket;

  public ClientChannel(final Channel channel,
                       final DatagramChannel socket) {
    this.channel = channel;
    this.socket = socket;
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
      receivedData.flip();
      return this.channel.onDataReceived(receivedData);
    } else {
      return null;
    }
  }

  public void sendPacket(final ByteBuffer data) throws IOException {
    this.channel.sendPacket(data);
  }
}
