package org.fryingpanjoe.bigbattle.client;

public class UpdateRateTimer {

  private float interval;
  private float time;

  public UpdateRateTimer(final float rate) {
    this.interval = 1.f / rate;
    this.time = 0.f;
  }

  public void setRate(final float rate) {
    this.interval = 1.f / rate;
  }

  public boolean shouldUpdate(final float timeDelta) {
    this.time += timeDelta;
    if (this.time >= this.interval) {
      this.time = 0.f;
      return true;
    } else {
      return false;
    }
  }
}
