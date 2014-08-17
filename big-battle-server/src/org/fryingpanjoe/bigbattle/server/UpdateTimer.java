package org.fryingpanjoe.bigbattle.server;

import org.lwjgl.Sys;

public class UpdateTimer {

  public static UpdateTimer fromMaxFps(final double maxFps) {
    return new UpdateTimer((long) (1000.0 / maxFps));
  }

  private final long updateInterval;
  private long updateTime;

  public UpdateTimer(final long updateInterval) {
    this.updateInterval = updateInterval;
    this.updateTime = Sys.getTime();
  }

  public long getTimeUntilUpdate() {
    final long now = Sys.getTime();
    final long timeUntilUpdate = Math.max(0, this.updateInterval - this.updateTime - now);
    if (timeUntilUpdate == 0) {
      this.updateTime = now;
    }
    return Math.max(0, timeUntilUpdate);
  }
}
