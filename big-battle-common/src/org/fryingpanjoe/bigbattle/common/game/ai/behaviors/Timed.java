package org.fryingpanjoe.bigbattle.common.game.ai.behaviors;

public class Timed implements Behavior {

  private final Behavior behavior;
  private float duration;

  public Timed(final Behavior behavior, final float duration) {
    this.behavior = behavior;
    this.duration = duration;
  }

  @Override
  public boolean apply(final float dt) {
    if (this.duration > 0.f) {
      this.behavior.apply(dt);
      this.duration -= dt;
    }
    return this.duration <= 0.f;
  }
}
