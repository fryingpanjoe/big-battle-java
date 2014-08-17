package org.fryingpanjoe.bigbattle.client;

import org.lwjgl.Sys;

public class FpsCounter {

  private static final long UPDATE_INTERVAL = 500;

  private long time;
  private long frames;
  private double fps;

  public FpsCounter() {
    this.time = Sys.getTime();
    this.frames = 0;
    this.fps = 0.0;
  }

  public boolean update() {
    ++this.frames;
    final long now = Sys.getTime();
    final long delta = now - this.time;
    if (delta >= UPDATE_INTERVAL) {
      this.fps = 1000.0 * (double) this.frames / (double) delta;
      this.time = now;
      this.frames = 0;
      return true;
    } else {
      return false;
    }
  }

  public double getFps() {
    return this.fps;
  }
}
