package org.fryingpanjoe.bigbattle.common.game.ai;

public class AITimer {

  private float timeLeft;

  public AITimer() {
    this.timeLeft = 0.f;
  }

  public void reset(final float duration) {
    this.timeLeft = duration;
  }

  public void update(final float dt) {
    this.timeLeft -= dt;
  }

  public boolean isDone() {
    return this.timeLeft <= 0.f;
  }
}
