package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Map;

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
      final InetSocketAddress connectAddress = new InetSocketAddress(
        InetAddress.getByName("192.168.1.176"), 12345);
      final DatagramChannel serverChannel = DatagramChannel.open();
      LOG.info("Connecting to " + connectAddress.getHostString() + ":" + connectAddress.getPort());
      serverChannel.configureBlocking(false);
      serverChannel.connect(connectAddress);
      final Selector selector = Selector.open();
      serverChannel.register(selector, SelectionKey.OP_READ);
      final EventBus eventBus = new EventBus("client-event-bus");
      eventBus.register(new Object() {
        @Subscribe
        public void onPacket(final Packet packet) {
          LOG.info("recv pck: " + packet);
        }
      });
      final Channel channel = new Channel(eventBus, serverChannel, connectAddress);
      final ClientConfig config = new ClientConfig();
      LOG.info("Setting up display");
      Display.setTitle(TITLE);
      Display.setDisplayMode(new DisplayMode(config.getDisplayWidth(), config.getDisplayHeight()));
      Display.setFullscreen(config.getDisplayFullscreen());
      Display.setVSyncEnabled(config.getDisplayVsync());
      Display.create();
      Defaults.setupGLInvariants();
      Defaults.setupViewportFromDisplay();
      final Activity activity = new TestTerrainActivity();
      long lastTime = Sys.getTime();
      long fpsTime = lastTime;
      long fpsFrameCount = 0;
      LOG.info("Entering main loop");
      while (!Display.isCloseRequested()) {
        final long now = Sys.getTime();

        if (selector.selectNow() > 0) {
          final ByteBuffer receivedData = ByteBuffer.allocate(512);
          final SocketAddress serverAddress = serverChannel.receive(receivedData);
          if (serverAddress != null) {
            receivedData.flip();
            channel.onDataReceived(receivedData);
            //eventBus.post(new ConnectedToServerEvent(clientId));
            //channel.sendPacket(data);
          } else {
            LOG.warning("DatagramChannel.receive() returned null");
          }
        }

        final long deltaTime = now - lastTime;
        lastTime = now;
        if (!activity.update((int)deltaTime)) {
          break;
        }

        Display.update();
        if (config.getDisplayFps().isPresent()) {
          Display.sync(config.getDisplayFps().get());
        }

        ++fpsFrameCount;
        final long deltaFpsTime = now - fpsTime;
        if (deltaFpsTime > 500) {
          final float fps = 1000.f * (float)fpsFrameCount / (float)deltaFpsTime;
          Display.setTitle(String.format("%s (%.2f FPS)", TITLE, fps));
          fpsFrameCount = 0;
          fpsTime = now;
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
