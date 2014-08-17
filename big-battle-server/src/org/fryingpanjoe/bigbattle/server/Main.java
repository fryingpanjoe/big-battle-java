package org.fryingpanjoe.bigbattle.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.common.networking.Channel;
import org.fryingpanjoe.bigbattle.common.networking.Packet;
import org.fryingpanjoe.bigbattle.server.config.ServerConfig;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class Main {

  private static final Logger LOG = Logger.getLogger(Main.class.getName());

  public static void main(final String[] argv) throws Exception {
    LOG.info("Starting big-battle-server");
    final ServerConfig config = new ServerConfig();
    final InetSocketAddress bindAddress = new InetSocketAddress(
      InetAddress.getByName(config.getBindAddress()), config.getBindPort());
    final DatagramChannel serverChannel = DatagramChannel.open();
    LOG.info("Binding to " + bindAddress.getHostString());
    serverChannel.bind(bindAddress);
    serverChannel.configureBlocking(false);
    final Selector selector = Selector.open();
    serverChannel.register(selector, SelectionKey.OP_READ);
    final EventBus eventBus = new EventBus("server-event-bus");
    eventBus.register(new Object() {
      @Subscribe
      public void onPacket(final Packet packet) {
        LOG.info("recv pck: " + packet);
      }
    });
    final FrameLimiter frameLimiter = new FrameLimiter(config.getMaxFps());
    final Map<SocketAddress, Channel> clientChannels = new HashMap<>();
    LOG.info("Entering main loop");
    int frame = 0;
    while (true) {
      if (selector.selectNow() > 0) {
        final ByteBuffer receivedData = ByteBuffer.allocate(512);
        final SocketAddress clientAddress = serverChannel.receive(receivedData);
        if (clientAddress != null) {
          final Channel clientChannel;
          if (clientChannels.containsKey(clientAddress)) {
            clientChannel = clientChannels.get(clientAddress);
          } else {
            LOG.info("Client connected: " + ((InetSocketAddress) clientAddress).getHostString());
            clientChannel = new Channel(eventBus, serverChannel, clientAddress);
          }
          receivedData.flip();
          clientChannel.onDataReceived(receivedData);
          //eventBus.post(new ClientConnectedEvent(clientId));
          //clientChannel.sendPacket(data);
        } else {
          LOG.warning("DatagramChannel.receive() returned null");
        }
      }

      final long timeUntilNextUpdate = frameLimiter.getTimeUntilNextUpdate();
      if (timeUntilNextUpdate > 0) {
        //Thread.sleep(timeUntilNextUpdate);
        Thread.sleep(1);
        continue;
      }

      final ByteBuffer deltaPacket = ByteBuffer.allocate(512);
      deltaPacket.putInt(frame);
      deltaPacket.flip();
      final Iterator<Channel> clientChannelIterator = clientChannels.values().iterator();
      while (clientChannelIterator.hasNext()) {
        try {
          clientChannelIterator.next().sendPacket(deltaPacket);
        } catch (final IOException e) {
          e.printStackTrace();
          LOG.info("Client disconnected");
          clientChannelIterator.remove();
        }
      }
    }
  }
}
