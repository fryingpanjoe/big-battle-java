package org.fryingpanjoe.bigbattle.client.rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector2f;

public class IsometricCamera {

  private float scale;
  private Vector2f pos;

  public IsometricCamera() {
    this.scale = 6.f;
    this.pos = new Vector2f(0.f, 0.f);
  }

  public void setScale(final float scale) {
    this.scale = scale;
  }

  public void setPos(final Vector2f pos) {
    this.pos = pos;
  }

  public void setPos(final float x, final float y) {
    this.pos.x = x;
    this.pos.y = y;
  }

  public float getScale() {
    return this.scale;
  }

  public Vector2f getPos() {
    return this.pos;
  }

  public void setupGL() {
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity();
    GL11.glOrtho(
      -this.scale, this.scale,
      -this.scale, this.scale,
      -1024.f, 1024.f);
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity();
    GLU.gluLookAt(
      this.pos.x + 0.75f, 1.f, this.pos.y + 0.75f,
      this.pos.x, 0.f, this.pos.y,
      0.f, 1.f, 0.f);
  }
}
