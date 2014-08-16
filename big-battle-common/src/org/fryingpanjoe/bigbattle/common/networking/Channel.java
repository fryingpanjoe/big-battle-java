package org.fryingpanjoe.bigbattle.common.networking;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;

import com.google.common.eventbus.EventBus;

public class Channel {

  private static final int MAX_PACKET_SIZE = 512;
  private static final int MAX_BACKLOG = 5;
  private static final int MAX_BUFFER_SIZE = MAX_BACKLOG * MAX_PACKET_SIZE;

  // must be <= number of ack bits
  private static final int WINDOW_SIZE = 32;

  private final EventBus eventBus;
  private final DatagramChannel channel;
  private final SocketAddress address;
  private final ByteBuffer recvBuffer;
  private int ackBits;
  private int localPacketId;
  private int remotePacketId;

  public Channel(final EventBus eventBus,
                 final DatagramChannel channel,
                 final SocketAddress address) {
    this.eventBus = eventBus;
    this.channel = channel;
    this.address = address;
    this.recvBuffer = ByteBuffer.allocate(MAX_BUFFER_SIZE).order(ByteOrder.BIG_ENDIAN);
    this.ackBits = 0;
    this.localPacketId = 0;
    this.remotePacketId = -1;
  }

  public void sendPacket(final ByteBuffer data) throws IOException {
    final int serializedPacketSize = 2 + 4 + 2 + data.remaining();
    if (serializedPacketSize > MAX_PACKET_SIZE) {
      throw new RuntimeException("Packet too big: " + serializedPacketSize);
    }
    final ByteBuffer packet = ByteBuffer.allocate(MAX_PACKET_SIZE)
      .order(ByteOrder.BIG_ENDIAN)
      .putShort((short)serializedPacketSize)
      .putInt(this.localPacketId)
      .putInt(this.remotePacketId)
      .putInt(this.ackBits)
      .putShort((short)data.remaining())
      .put(data);
    if (this.channel.send(packet, this.address) == 0) {
      throw new IOException("No bytes sent");
    }
    ++this.localPacketId;
  }

  public void onDataReceived(final ByteBuffer data) {
    this.recvBuffer.put(data);
    if (this.recvBuffer.position() > 2) {
      this.recvBuffer.mark();
      final short packetSize = this.recvBuffer.getShort(0);
      if (this.recvBuffer.position() >= packetSize) {
        this.recvBuffer.flip();
        this.recvBuffer.getShort();
        final int packetId = this.recvBuffer.getInt();
        final int ackPacketId = this.recvBuffer.getInt();
        final int ackBits = this.recvBuffer.getInt();
        final short packetDataSize = this.recvBuffer.getShort();
        final byte[] packetData = new byte[packetDataSize];
        this.recvBuffer.get(packetData, 0, packetDataSize);
        this.recvBuffer.compact();
        onPacketReceived(packetId, ackPacketId, ackBits, packetData);
      } else {
        this.recvBuffer.reset();
      }
    }
  }

  private void onPacketReceived(final int packetId,
                                final int ackPacketId,
                                final int ackBits,
                                final byte[] packetData) {
    if (packetId < this.remotePacketId) {
      final int bit = this.remotePacketId - packetId - 1;
      if (bit >= WINDOW_SIZE) {
        // packet too old, ignore it
        return;
      } else {
        this.ackBits |= bit;
      }
    } else if (packetId > this.remotePacketId) {
      this.ackBits = shiftAckBits(this.ackBits, packetId - this.remotePacketId);
      this.remotePacketId = packetId;
    }
    this.eventBus.post(new Packet(packetId, ackPacketId, ackBits, packetData));
  }

  private static int shiftAckBits(final int ack, final int shift) {
    return shift < WINDOW_SIZE ? (ack << shift) : 0;
  }
}
