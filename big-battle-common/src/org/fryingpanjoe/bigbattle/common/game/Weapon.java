package org.fryingpanjoe.bigbattle.common.game;

public class Weapon {

  private final int id;
  private final float damage;
  private final float range;
  private final float delay;

  public Weapon(final int id, final float damage, final float range, final float delay) {
    this.id = id;
    this.damage = damage;
    this.range = range;
    this.delay = delay;
  }

  public int getId() {
    return this.id;
  }

  public float getDamage() {
    return this.damage;
  }

  public float getRange() {
    return this.range;
  }

  public float getDelay() {
    return this.delay;
  }
}
