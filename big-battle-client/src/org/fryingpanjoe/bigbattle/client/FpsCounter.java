package org.fryingpanjoe.bigbattle.client;

public class FpsCounter {

  private static final float UPDATE_INTERVAL = 0.5f;

  private float time;
  private long frames;
  private float fps;

  public FpsCounter() {
    this.time = 0.f;
    this.frames = 0;
    this.fps = 0.f;
  }

  public boolean update(final float timeDelta) {
    ++this.frames;
    this.time += timeDelta;
    if (this.time >= UPDATE_INTERVAL) {
      this.fps = (float) this.frames / this.time;
      this.time = 0.f;
      this.frames = 0;
      return true;
    } else {
      return false;
    }
  }

  public float getFps() {
    return this.fps;
  }
}
