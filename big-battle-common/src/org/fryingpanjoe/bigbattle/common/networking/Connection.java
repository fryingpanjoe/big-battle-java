package org.fryingpanjoe.bigbattle.common.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class Connection {

  private final DatagramSocket socket;
  private final SocketAddress address;

  public Connection(final DatagramSocket socket,
                    final SocketAddress address) {
    this.socket = socket;
    this.address = address;
  }

  public void sendData(final byte[] data) throws IOException {
    this.socket.send(new DatagramPacket(data, data.length, this.address));
  }

  public void receiveData(final byte[] data) throws IOException {
    this.socket.receive(new DatagramPacket(data, data.length));
  }
}
