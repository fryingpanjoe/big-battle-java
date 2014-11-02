package org.fryingpanjoe.bigbattle.common.game.ai.behaviors;

import org.fryingpanjoe.bigbattle.common.game.Entity;

public class Follow implements Behavior {

  private final Entity entity;
  private final Entity followed;

  public Follow(final Entity entity, final Entity followed) {
    this.entity = entity;
    this.followed = followed;
  }

  @Override
  public boolean apply(final float dt) {
    final float dx = this.followed.getX() - this.entity.getX();
    final float dy = this.followed.getY() - this.entity.getY();
    final float squaredDistance = dx * dx + dy * dy;
    final float closestDistance =
      this.followed.getDefinition().getRadius() + this.entity.getDefinition().getRadius();
    if (Math.abs(squaredDistance) <= (closestDistance * closestDistance)) {
      this.entity.setVelocity(0.f, 0.f);
      return false;
    } else {
      final float distance = (float) Math.sqrt(squaredDistance);
      final float velx = dx * this.entity.getDefinition().getSpeed() / distance;
      final float vely = dy * this.entity.getDefinition().getSpeed() / distance;
      this.entity.setVelocity(velx, vely);
      return true;
    }
  }
}
