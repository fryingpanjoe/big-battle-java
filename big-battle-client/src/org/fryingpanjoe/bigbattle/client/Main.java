package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import org.fryingpanjoe.bigbattle.client.activities.Activity;
import org.fryingpanjoe.bigbattle.client.activities.TestTerrainActivity;
import org.fryingpanjoe.bigbattle.client.config.ClientConfig;
import org.fryingpanjoe.bigbattle.client.rendering.Defaults;
import org.fryingpanjoe.bigbattle.common.networking.Channel;
import org.fryingpanjoe.bigbattle.common.networking.Packet;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.sun.istack.internal.logging.Logger;

public class Main {

  private static final Logger LOG = Logger.getLogger(Main.class);

  private static final String TITLE = "Big Battle Client";

  public static void main(final String[] argv) {
    try {
      LOG.info("Starting big-battle-client");

      final ClientConfig config = new ClientConfig();

      LOG.info("Initializing display");
      Display.setTitle(TITLE);
      Display.setDisplayMode(new DisplayMode(config.getDisplayWidth(), config.getDisplayHeight()));
      Display.setFullscreen(config.getDisplayFullscreen());
      Display.setVSyncEnabled(config.getDisplayVsync());
      Display.create();

      Defaults.setupGLInvariants();
      Defaults.setupViewportFromDisplay();

      final InetSocketAddress connectAddress = new InetSocketAddress(
        InetAddress.getByName("192.168.1.176"), 12345);
      LOG.info("Connecting to " + connectAddress.getHostString() + ":" + connectAddress.getPort());
      final DatagramChannel serverChannel = DatagramChannel.open();
      serverChannel.configureBlocking(false);
      serverChannel.connect(connectAddress);
      final EventBus eventBus = new EventBus("client-event-bus");
      eventBus.register(new Object() {
        @Subscribe
        public void onPacket(final Packet packet) {
          LOG.info("recv pck: " + packet);
        }
      });
      final Channel channel = new Channel(eventBus, serverChannel, connectAddress);

      final ByteBuffer firstPacket = Channel.createPacketBuffer();
      firstPacket.putInt(0xdeadbeef);
      firstPacket.flip();
      channel.sendPacket(firstPacket);

      final Activity activity = new TestTerrainActivity();

      long gameUpdatedAt = Sys.getTime();

      final FpsCounter fpsCounter = new FpsCounter();
      while (!Display.isCloseRequested()) {
        final long now = Sys.getTime();

        // send input to server

        // receive update from server
        final ByteBuffer receivedData = Channel.createPacketBuffer();
        final SocketAddress serverAddress = serverChannel.receive(receivedData);
        if (serverAddress != null) {
          receivedData.flip();
          channel.onDataReceived(receivedData);
        }

        // update game
        final long deltaUpdateTime = now - gameUpdatedAt;
        if (deltaUpdateTime > 0) {
          gameUpdatedAt = now;
          if (!activity.update(deltaUpdateTime)) {
            break;
          }
        }

        // update display
        Display.update();
        if (config.getDisplayFps().isPresent()) {
          Display.sync(config.getDisplayFps().get());
        }

        if (fpsCounter.update()) {
          Display.setTitle(String.format("%s (%d FPS)", TITLE, fpsCounter.getFps()));
        }
      }
    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final LWJGLException e) {
      e.printStackTrace();
      System.exit(0);
    } finally {
      Display.destroy();
    }
  }
}
