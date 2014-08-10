package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;

import org.fryingpanjoe.bigbattle.client.activities.Activity;
import org.fryingpanjoe.bigbattle.client.activities.TestTerrainActivity;
import org.fryingpanjoe.bigbattle.client.config.ClientConfig;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

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
      final Activity activity = new TestTerrainActivity();
      long lastTime = Sys.getTime();
      long fpsTime = lastTime;
      long frameCount = 0;
      GL11.glShadeModel(GL11.GL_NICEST);
      GL11.glDepthFunc(GL11.GL_LEQUAL);
      GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
      while (!Display.isCloseRequested()) {
        final long now = Sys.getTime();
        final long frameTime = now - lastTime;
        lastTime = now;
        if (!activity.update((int) frameTime)) {
          break;
        }
        Display.update();
        if (config.getDisplayFps().isPresent()) {
          Display.sync(config.getDisplayFps().get());
        }
        ++frameCount;
        if ((now - fpsTime) > 500) {
          final float fps = 1000.f * (float)frameCount / (float)(now - fpsTime);
          Display.setTitle(String.format("%s (%.2f FPS)", TITLE, fps));
          frameCount = 0;
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
