package org.fryingpanjoe.bigbattle.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
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
    LOG.info("Binding to " + bindAddress.getHostString() + ":" + bindAddress.getPort());

    final DatagramChannel serverChannel = DatagramChannel.open();
    serverChannel.configureBlocking(false);
    serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
    serverChannel.bind(bindAddress);
    final EventBus eventBus = new EventBus("server-event-bus");
    eventBus.register(new Object() {
      @Subscribe
      public void onPacket(final Packet packet) {
        LOG.info("recv pck: " + packet);
      }
    });

    final Map<SocketAddress, Channel> clientChannels = new HashMap<>();

    int frame = 0;

    final UpdateTimer serverFrameTimer = UpdateTimer.fromMaxFps(config.getMaxFps());
    while (true) {

      // receive data from clients
      while (true) {
        final ByteBuffer receivedData = ByteBuffer.allocate(512).order(ByteOrder.BIG_ENDIAN);
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
        } else {
          break;
        }
      }

      final long timeUntilUpdate = serverFrameTimer.getTimeUntilUpdate();
      if (timeUntilUpdate > 0) {
        //Thread.sleep(timeUntilUpdate);
        Thread.sleep(1);
        continue;
      }

      // compute delta
      final ByteBuffer deltaPacket = ByteBuffer.allocate(512).order(ByteOrder.BIG_ENDIAN);
      deltaPacket.putInt(frame);
      deltaPacket.flip();

      // send data to clients
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
