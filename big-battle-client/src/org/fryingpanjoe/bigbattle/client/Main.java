package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;

import org.fryingpanjoe.bigbattle.client.activities.Activity;
import org.fryingpanjoe.bigbattle.client.activities.TestTerrainActivity;
import org.fryingpanjoe.bigbattle.client.config.ClientConfig;
import org.fryingpanjoe.bigbattle.client.rendering.Defaults;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Main {

  private static final String TITLE = "Big Battle Client";

  public static void main(final String[] argv) {
    final ClientConfig config = new ClientConfig();
    try {
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
      while (!Display.isCloseRequested()) {
        final long now = Sys.getTime();

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
