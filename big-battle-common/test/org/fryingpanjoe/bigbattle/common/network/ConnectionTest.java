package org.fryingpanjoe.bigbattle.common.network;

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import junit.framework.TestCase;

import org.fryingpanjoe.bigbattle.common.networking.Connection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionTest extends TestCase {

  @Mock private DatagramSocket socket;
  @Mock private SocketAddress address;

  private Connection conn;

  @Before
  public void before() {
    //this.conn = new Connection(this.socket, this.address);
  }

  @Test
  public void testReceiveData() {
    /*final short packetSize = 2 + 4 + 2 + 5;
    final ByteBuffer packet = ByteBuffer.allocate(packetSize);
    packet.order(ByteOrder.BIG_ENDIAN);
    packet.putShort(packetSize);
    packet.putInt(3);
    packet.putShort((short)5);
    packet.put("HELLO".getBytes());
    packet.flip();
    this.conn.onDataReceived(packet.array());
    assertTrue(this.conn.hasReceivedPacket());
    assertEquals(new Packet(3, "HELLO".getBytes()), this.conn.getReceivedPacket());*/
  }

  @Test
  public void testShift() {
    /*System.out.println("SHIFT");
    final int ack = 0b11111111000011110011001101010101;
    System.out.println(String.format("%32s", Integer.toBinaryString(ack)).replace(' ', '0'));
    System.out.println(String.format("%32s", Integer.toBinaryString(ack << 1)).replace(' ', '0'));
    System.out.println(String.format("%32s", Integer.toBinaryString(ack << 8)).replace(' ', '0'));
    System.out.println(String.format("%32s", Integer.toBinaryString(ack << 16)).replace(' ', '0'));
    System.out.println(String.format("%32s", Integer.toBinaryString(ack << 24)).replace(' ', '0'));
    System.out.println(String.format("%32s", Integer.toBinaryString((ack << 31) << 1)).replace(' ', '0'));
    System.out.println("OR");
    System.out.println(String.format("%32s", Integer.toBinaryString((int)0b00000000000000000000000000000000 | (int)(1 << 0))).replace(' ', '0'));
    System.out.println(String.format("%32s", Integer.toBinaryString((int)0b00000000000000000000000000000000 | (int)(1 << 1))).replace(' ', '0'));
    System.out.println(String.format("%32s", Integer.toBinaryString((int)0b00000000000000000000000000000000 | (int)(1 << 8))).replace(' ', '0'));
    System.out.println(String.format("%32s", Integer.toBinaryString((int)0b00000000000000000000000000000000 | (int)(1 << 16))).replace(' ', '0'));
    System.out.println(String.format("%32s", Integer.toBinaryString((int)0b00000000000000000000000000000000 | (int)(1 << 32))).replace(' ', '0'));
    System.out.println(String.format("%32s", Integer.toBinaryString((int)0b00000000000000000000000000000000 | (int)(1 << 50))).replace(' ', '0'));
    System.out.println(String.format("%32s", Integer.toBinaryString((int)0b00000000000000000000000000000000 | (int)(1 << 33))).replace(' ', '0'));
    System.out.println(String.format("%32s", Integer.toBinaryString((int)0b00000000000000000000000000000000 | (int)(1 << 34))).replace(' ', '0'));
    System.out.println(String.format("%32s", Integer.toBinaryString((int)0b00000000000000000000000000000000 | (int)(1 << 64))).replace(' ', '0'));*/
  }
}
