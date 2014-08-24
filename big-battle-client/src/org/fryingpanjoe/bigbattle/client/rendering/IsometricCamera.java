package org.fryingpanjoe.bigbattle.client.rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector2f;

public class IsometricCamera {

  private float scale;
  private Vector2f position;

  public IsometricCamera() {
    this.scale = 6.f;
    this.position = new Vector2f(0.f, 0.f);
  }

  public void setScale(final float scale) {
    this.scale = scale;
  }

  public void setPosition(final float x, final float y) {
    this.position.x = x;
    this.position.y = y;
  }

  public float getScale() {
    return this.scale;
  }

  public Vector2f getPosition() {
    return this.position;
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
      this.position.x + 0.75f, 1.f, this.position.y + 0.75f,
      this.position.x, 0.f, this.position.y,
      0.f, 1.f, 0.f);
  }
}
