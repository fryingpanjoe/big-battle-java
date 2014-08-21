package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.common.networking.Channel;
import org.fryingpanjoe.bigbattle.common.networking.Packet;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class ClientNetworkManager {

  private static final Logger LOG = Logger.getLogger(ClientNetworkManager.class.getName());

  public static ClientNetworkManager fromHostPort(final String host,
                                                  final int port,
                                                  final EventBus eventBus) throws IOException {
    final InetSocketAddress connectAddress = new InetSocketAddress(
      InetAddress.getByName(host), port);
    LOG.info("Connecting to " + connectAddress.getHostString() + ":" + connectAddress.getPort());
    final DatagramChannel serverChannel = DatagramChannel.open();
    serverChannel.configureBlocking(false);
    serverChannel.connect(connectAddress);
    final Channel channel = new Channel(eventBus, serverChannel, connectAddress);
    return new ClientNetworkManager(channel, eventBus);
  }

  private final Channel channel;
  private final EventBus eventBus;

  public ClientNetworkManager(final Channel channel, final EventBus eventBus) {
    this.channel = channel;
    this.eventBus = eventBus;
  }

  @Subscribe
  public void onPacketReceived(final Packet packet) {
  }
}
