package org.fryingpanjoe.bigbattle.client.rendering;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class OpenGLDefaults {

  public static void setupGLInvariants() {
    GL11.glShadeModel(GL11.GL_NICEST);
    GL11.glDepthFunc(GL11.GL_LEQUAL);
  }

  public static void setupViewportFromDisplay() {
    GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
  }
}
