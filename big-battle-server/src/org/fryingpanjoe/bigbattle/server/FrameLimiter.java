package org.fryingpanjoe.bigbattle.server;

public class FrameLimiter {

  private final long minFrameTime;
  private long updateTime;

  public FrameLimiter(final double maxFps) {
    this.minFrameTime = (long)(1000.0 / maxFps);
    this.updateTime = System.currentTimeMillis();
  }

  public boolean shouldUpdate() {
    return getTimeUntilNextUpdate() == 0;
  }

  public long getTimeUntilNextUpdate() {
    final long now = System.currentTimeMillis();
    final long timeSinceUpdate = now - this.updateTime;
    final long timeUntilNextUpdate = Math.max(
      0, this.minFrameTime - timeSinceUpdate);
    if (timeUntilNextUpdate == 0) {
      this.updateTime = now;
      return 0;
    } else {
      return timeUntilNextUpdate;
    }
  }
}
