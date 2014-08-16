package org.fryingpanjoe.bigbattle.common.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Connection {

  private final DatagramSocket socket;
  private final SocketAddress remoteAddress;
  private final ByteBuffer recvBuffer;
  private final List<byte[]> outbox;
  private final List<Packet> inbox;

  public Connection(final DatagramSocket socket,
                    final SocketAddress remoteAddress,
                    final int bufferSize) {
    this.socket = socket;
    this.remoteAddress = remoteAddress;
    this.recvBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
    this.recvBuffer.order(ByteOrder.BIG_ENDIAN);
    this.outbox = new LinkedList<>();
    this.inbox = new LinkedList<>();
  }

  public void sendPacket(final int packetId, final byte[] packetData) {
    final int serializedPacketSize = 2 + 4 + 2 + packetData.length;
    if (serializedPacketSize > MAX_PACKET_SIZE) {
      throw new RuntimeException("Packet too big: " + serializedPacketSize);
    }
    final ByteBuffer serializedPacket = ByteBuffer.allocate(serializedPacketSize)
      .order(ByteOrder.BIG_ENDIAN)
      .putShort((short)serializedPacketSize)
      .putInt(packetId)
      .putShort((short)packetData.length)
      .put(packetData);
    serializedPacket.flip();
    this.outbox.add(serializedPacket.array());
  }

  public boolean hasReceivedPacket() {
    return !this.inbox.isEmpty();
  }

  public Packet getReceivedPacket() {
    if (this.inbox.isEmpty()) {
      return null;
    } else {
      return this.inbox.remove(0);
    }
  }

  public boolean onDataSend() {
    try {
      final Iterator<byte[]> iterator = this.outbox.iterator();
      while (iterator.hasNext()) {
        final byte[] packet = iterator.next();
        this.socket.send(new DatagramPacket(packet, packet.length, this.remoteAddress));
        iterator.remove();
      }
      return true;
    } catch (final IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public void onDataReceived(final byte[] data) {
    this.recvBuffer.put(data);
    if (this.recvBuffer.position() > 2) {
      this.recvBuffer.mark();
      final short packetSize = this.recvBuffer.getShort(0);
      if (this.recvBuffer.position() >= packetSize) {
        this.recvBuffer.flip();
        this.recvBuffer.getShort();
        final int packetId = this.recvBuffer.getInt();
        final short packetDataSize = this.recvBuffer.getShort();
        final byte[] packetData = new byte[packetDataSize];
        this.recvBuffer.get(packetData, 0, packetDataSize);
        this.inbox.add(new Packet(packetId, packetData));
        this.recvBuffer.compact();
      } else {
        this.recvBuffer.reset();
      }
    }
  }
}
