package org.fryingpanjoe.bigbattle.client;

import org.lwjgl.Sys;

public class UpdateRateTimer {

  public static UpdateRateTimer fromFps(final float fps) {
    return new UpdateRateTimer((long) (1.f / fps));
  }

  private final long interval;
  private long time;

  private UpdateRateTimer(final long interval) {
    this.interval = interval;
    this.time = Sys.getTime();
  }

  public boolean shouldUpdate() {
    final long now = Sys.getTime();
    final long timeSinceUpdate = now - this.time;
    if (timeSinceUpdate >= this.interval) {
      this.time = now;
      return true;
    } else {
      return false;
    }
  }
}
