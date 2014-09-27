package org.fryingpanjoe.bigbattle.common.game.ai.behaviors;

import java.util.Random;

import org.fryingpanjoe.bigbattle.common.game.Entity;
import org.fryingpanjoe.bigbattle.common.game.ai.AITimer;

public class Roam implements Behavior {

  private static final Random SHARED_RANDOM = new Random();

  private final Entity entity;
  private final float range;
  private final float delay;
  private final AITimer timer;
  private MoveTo moveTo;

  public Roam(final Entity entity, final float range, final float delay) {
    this.entity = entity;
    this.range = range;
    this.delay = delay;
    this.timer = new AITimer();
    this.moveTo = null;
  }

  @Override
  public boolean apply(final float dt) {
    if (this.timer.isDone()) {
      if (this.moveTo == null || !this.moveTo.apply(dt)) {
        this.moveTo = generateMoveTo();
        this.timer.reset(gaussian(this.delay));
      }
    } else {
      this.timer.update(dt);
    }
    return true;
  }

  private MoveTo generateMoveTo() {
    final float distance = gaussian(this.range);
    final float direction = (float) (SHARED_RANDOM.nextDouble() * 2.0 * Math.PI);
    final float x = this.entity.getX() + distance * (float) Math.cos(direction);
    final float y = this.entity.getY() + distance * (float) Math.sin(direction);
    return new MoveTo(this.entity, x, y);
  }

  private static float gaussian(final float value) {
    final float factor = (float) (1.0 + 0.5 * SHARED_RANDOM.nextGaussian());
    final float clampedFactor = (float) Math.max(0.0, Math.min(2.0, factor));
    return clampedFactor * value;
  }
}
