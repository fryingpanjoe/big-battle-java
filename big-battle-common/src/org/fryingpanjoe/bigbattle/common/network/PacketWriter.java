package org.fryingpanjoe.bigbattle.common.network;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketWriter {

  private final int packetId;
  private final int maxPacketSize;
  private final ByteBuffer buffer;

  public PacketWriter(final int packetId, final int maxPacketSize) {
    this.packetId = packetId;
    this.maxPacketSize = maxPacketSize;
    this.buffer = ByteBuffer.allocate(this.maxPacketSize);
    this.buffer.order(ByteOrder.BIG_ENDIAN);
  }

  public int getPacketId() {
    return this.packetId;
  }

  public int getMaxPacketSize() {
    return this.maxPacketSize;
  }

  public Packet getPacket() {
    this.buffer.flip();
    return new Packet(this.packetId, this.buffer.array());
  }

  public PacketWriter writeBytes(final byte[] v) {
    this.buffer.put(v);
    return this;
  }

  public PacketWriter writeInt8(final byte v) {
    this.buffer.put(v);
    return this;
  }

  public PacketWriter writeInt16(final short v) {
    this.buffer.putShort(v);
    return this;
  }

  public PacketWriter writeInt32(final int v) {
    this.buffer.putInt(v);
    return this;
  }

  public PacketWriter writeInt64(final long v) {
    this.buffer.putLong(v);
    return this;
  }

  public PacketWriter writeFloat(final float v) {
    this.buffer.putFloat(v);
    return this;
  }

  public PacketWriter writeDouble(final double v) {
    this.buffer.putDouble(v);
    return this;
  }
}
