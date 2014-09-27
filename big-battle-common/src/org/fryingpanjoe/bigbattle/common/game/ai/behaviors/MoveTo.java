package org.fryingpanjoe.bigbattle.common.game.ai.behaviors;

import org.fryingpanjoe.bigbattle.common.game.Entity;

public class MoveTo implements Behavior {

  private static final float VERY_CLOSE = 0.1f;

  private final Entity entity;
  private final float x;
  private final float y;

  public MoveTo(final Entity entity, final float x, final float y) {
    this.entity = entity;
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean apply(final float dt) {
    final float dx = this.x - this.entity.getX();
    final float dy = this.y - this.entity.getY();
    final float squaredDistance = dx * dx + dy * dy;
    if (Math.abs(squaredDistance) <= VERY_CLOSE) {
      this.entity.setVelocity(0.f, 0.f);
      return false;
    } else {
      final float distance = (float) Math.sqrt(squaredDistance);
      final float velx = dx * this.entity.getDefinition().getSpeed() / distance;
      final float vely = dy * this.entity.getDefinition().getSpeed() / distance;
      final float rotation = (float) Math.atan2(-vely, velx);
      this.entity.setVelocity(velx, vely);
      this.entity.setRotation(rotation);
      return true;
    }
  }
}
