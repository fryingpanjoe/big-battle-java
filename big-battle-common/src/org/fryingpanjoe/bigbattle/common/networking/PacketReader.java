package org.fryingpanjoe.bigbattle.common.networking;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketReader {

  private final int packetId;
  private final ByteBuffer buffer;

  public PacketReader(final int packetId, final byte[] data) {
    this.packetId = packetId;
    this.buffer = ByteBuffer.allocate(data.length);
    this.buffer.order(ByteOrder.BIG_ENDIAN);
    this.buffer.get(data);
    this.buffer.flip();
  }

  public int getPacketId() {
    return this.packetId;
  }

  public byte[] readBytes(final byte[] v) {
    this.buffer.get(v);
    return v;
  }

  public byte readInt8() {
    return this.buffer.get();
  }

  public short readInt16() {
    return this.buffer.getShort();
  }

  public int readInt32() {
    return this.buffer.getInt();
  }

  public long readInt64() {
    return this.buffer.getLong();
  }

  public float readFloat() {
    return this.buffer.getFloat();
  }

  public double readDouble() {
    return this.buffer.getDouble();
  }
}
