package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.common.networking.Channel;

import com.google.common.eventbus.EventBus;

public class ClientNetworkManager {

  private static final Logger LOG = Logger.getLogger(ClientNetworkManager.class.getName());

  private final EventBus eventBus;
  private DatagramChannel serverSocket;
  private InetSocketAddress serverAddress;
  private Channel channel;

  public ClientNetworkManager(final EventBus eventBus) {
    this.eventBus = eventBus;
  }

  public void update() {
    try {
      final ByteBuffer receivedData = Channel.createPacketBuffer();
      final SocketAddress serverAddress = this.serverSocket.receive(receivedData);
      if (serverAddress != null) {
        receivedData.flip();
        this.channel.onDataReceived(receivedData);
      }
    } catch (final IOException e) {
      //
    }
  }

  public void connect(final String host, final int port) throws IOException {
    disconnect();

    LOG.info(String.format("Connecting to %s:%d", host, port));

    this.serverAddress = new InetSocketAddress(InetAddress.getByName(host), port);
    this.serverSocket = DatagramChannel.open();
    this.serverSocket.configureBlocking(false);
    this.serverSocket.connect(this.serverAddress);
    this.channel = new Channel(this.serverSocket, this.serverAddress);

    // send hello
  }

  public void disconnect() {
    LOG.info("Disconnecting");

    // send goodbye

    this.serverAddress = null;
    if (this.serverSocket != null && this.serverSocket.isOpen()) {
      try {
        this.serverSocket.close();
      } catch (final IOException e) {
        //
      }
    }
    this.serverSocket = null;
    this.channel = null;
  }
}
