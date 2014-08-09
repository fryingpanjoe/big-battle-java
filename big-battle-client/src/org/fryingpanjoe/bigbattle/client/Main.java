package org.fryingpanjoe.bigbattle.client;

import org.fryingpanjoe.bigbattle.client.config.ClientConfig;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Main {

  public static void main(final String[] argv) {
    final ClientConfig config = new ClientConfig();
    try {
      Display.setTitle("Big Battle Java");
      Display.setDisplayMode(new DisplayMode(config.getDisplayWidth(), config.getDisplayHeight()));
      Display.setFullscreen(config.getDisplayFullscreen());
      Display.setVSyncEnabled(config.getDisplayVsync());
      Display.create();
    } catch (final LWJGLException e) {
      e.printStackTrace();
      System.exit(0);
    }
    while (!Display.isCloseRequested()) {
      Display.update();
    }
    Display.destroy();
  }
}
