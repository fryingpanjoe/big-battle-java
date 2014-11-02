package org.fryingpanjoe.bigbattle.common.game.ai.behaviors;

import org.fryingpanjoe.bigbattle.common.game.Entity;

public class Attack implements Behavior {

  private final Entity attacker;
  private final Entity target;
  private final Follow follow;

  public Attack(final Entity attacker, final Entity target) {
    this.attacker = attacker;
    this.target = target;
    this.follow = new Follow(attacker, target);
  }

  @Override
  public boolean apply(final float dt) {
    this.follow.apply(dt);
    final float dx = this.target.getX() - this.attacker.getX();
    final float dy = this.target.getY() - this.attacker.getY();
    final float squaredDistance = dx * dx + dy * dy;
    final float weaponRange = this.attacker.getWeapon().getRange();
    if (squaredDistance <= (weaponRange * weaponRange)) {
      final float rotation = (float) Math.atan2(dy, dx);
      this.attacker.setRotation(rotation);
      this.attacker.setState(Entity.State.Attacking);
    } else {
      this.attacker.setState(Entity.State.Idle);
    }
    return this.target.getHealth() > 0.f;
  }
}
