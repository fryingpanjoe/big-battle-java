package org.fryingpanjoe.bigbattle.common.networking;

import java.util.Arrays;

public class Packet {

  private final int id;
  private final int ack;
  private final int ackBits;
  private final byte[] data;

  public Packet(final int id, final int ack, final int ackBits, final byte[] data) {
    this.id = id;
    this.ack = ack;
    this.ackBits = ackBits;
    this.data = data;
  }

  public int getId() {
    return this.id;
  }

  public int getAck() {
    return this.ack;
  }

  public int getAckBits() {
    return this.ackBits;
  }

  public byte[] getData() {
    return this.data;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.id;
    result = prime * result + this.ack;
    result = prime * result + this.ackBits;
    result = prime * result + Arrays.hashCode(this.data);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Packet other = (Packet) obj;
    if (this.id != other.id) {
      return false;
    }
    if (this.ack != other.ack) {
      return false;
    }
    if (this.ackBits != other.ackBits) {
      return false;
    }
    if (!Arrays.equals(this.data, other.data)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Packet [id=" + this.id +
      ", ack=" + this.ack +
      ", ackBits=" + this.ackBits +
      ", data=" + Arrays.toString(this.data) +
      "]";
  }
}
