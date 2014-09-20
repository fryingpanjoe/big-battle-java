package org.fryingpanjoe.bigbattle.common.networking;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;

public class Channel {

  private static final Logger LOG = Logger.getLogger(Channel.class.getName());

  private static final int MAX_PACKET_SIZE = 512;

  // must be <= number of ack bits
  private static final int ACK_WINDOW_SIZE = 32;

  private final DatagramChannel socket;
  private final SocketAddress address;
  private int ackBits;
  private int localPacketId;
  private int remotePacketId;

  public Channel(final DatagramChannel socket, final SocketAddress address) {
    this.socket = socket;
    this.address = address;
    this.ackBits = 0;
    this.localPacketId = 0;
    this.remotePacketId = -1;
  }

  public static ByteBuffer createPacketBuffer() {
    return ByteBuffer.allocate(MAX_PACKET_SIZE).order(ByteOrder.BIG_ENDIAN);
  }

  public void sendPacket(final ByteBuffer data) throws IOException {
    final int serializedPacketSize = 2 + 4 + 4 + 4 + 2 + data.remaining();
    if (serializedPacketSize > MAX_PACKET_SIZE) {
      throw new RuntimeException("Packet too big: " + serializedPacketSize);
    }
    final ByteBuffer packet = createPacketBuffer()
      .putShort((short)serializedPacketSize)
      .putInt(this.localPacketId)
      .putInt(this.remotePacketId)
      .putInt(this.ackBits)
      .putShort((short)data.remaining())
      .put(data);
    packet.flip();
    if (this.socket.send(packet, this.address) == 0) {
      throw new IOException("No bytes sent");
    }
    ++this.localPacketId;
  }

  public Packet onDataReceived(final ByteBuffer data) {
    if (data.limit() > 2) {
      final short packetSize = data.getShort();
      if (data.limit() >= packetSize) {
        final int packetId = data.getInt();
        final int ackPacketId = data.getInt();
        final int ackBits = data.getInt();
        final short packetDataSize = data.getShort();
        final byte[] packetData = new byte[packetDataSize];
        data.get(packetData);
        return onPacketReceived(packetId, ackPacketId, ackBits, packetData);
      } else {
        LOG.warning(String.format("Bad packet received: size %d < %d", data.limit(), packetSize));
      }
    } else {
      LOG.warning("Bad packet received: missing header");
    }
    return null;
  }

  private Packet onPacketReceived(final int receivedPacketId,
                                  final int receivedAckPacketId,
                                  final int receivedAckBits,
                                  final byte[] receivedPacketData) {
    if (receivedPacketId < this.remotePacketId) {
      final int bit = this.remotePacketId - receivedPacketId - 1;
      if (bit >= ACK_WINDOW_SIZE) {
        // packet too old, ignore it
        return null;
      } else {
        this.ackBits |= bit;
      }
    } else if (receivedPacketId > this.remotePacketId) {
      this.ackBits = shiftAckBits(this.ackBits, receivedPacketId - this.remotePacketId);
      this.remotePacketId = receivedPacketId;
    }
    return new Packet(receivedPacketId, receivedAckPacketId, receivedAckBits, receivedPacketData);
  }

  private static int shiftAckBits(final int ack, final int shift) {
    return shift < ACK_WINDOW_SIZE ? (ack << shift) : 0;
  }
}
