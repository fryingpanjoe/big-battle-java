package org.fryingpanjoe.bigbattle.common.networking;

public class FlowControl {

  private static final float GOOD_SEND_RATE = 30.f;
  private static final float BAD_SEND_RATE = 10.f;

  private static final int GOOD_RTT = 250;

  private static final int MAX_RECOVERY_TIME = 60000;
  private static final int MIN_RECOVERY_TIME = 1000;
  private static final int FLAP_TIME = 10000;
  private static final int GOOD_BEHAVIOR_TIME = 10000;

  private int recoveryTime;
  private int timer;
  private boolean good;

  public FlowControl() {
    this.recoveryTime = MIN_RECOVERY_TIME;
    this.timer = 0;
    this.good = true;
  }

  public void update(final int deltaTime, final int rtt) {
    if (rtt >= GOOD_RTT) {
      if (this.good) {
        this.timer += deltaTime;
        if (this.timer >= GOOD_BEHAVIOR_TIME) {
          this.recoveryTime = Math.min(this.recoveryTime / 2, MIN_RECOVERY_TIME);
          this.timer = 0;
        }
      } else {
        this.timer += deltaTime;
        if (this.timer >= this.recoveryTime) {
          this.good = true;
          this.timer = 0;
        }
      }
    } else {
      if (this.good) {
        if (this.timer <= FLAP_TIME) {
          this.recoveryTime = Math.min(this.recoveryTime * 2, MAX_RECOVERY_TIME);
        }
        this.good = false;
        this.timer = 0;
      } else {
        this.timer += deltaTime;
      }
    }
  }

  public float getSendRate() {
    if (this.good) {
      return GOOD_SEND_RATE;
    } else {
      return BAD_SEND_RATE;
    }
  }
}
