package org.fryingpanjoe.bigbattle.common.game;

public class EntityDefinition {

  private final int id;
  private final float mass;
  private final float radius;

  public EntityDefinition(final int id,
                          final float mass,
                          final float radius) {
    this.id = id;
    this.mass = mass;
    this.radius = radius;
  }

  public int getId() {
    return this.id;
  }

  public float getMass() {
    return this.mass;
  }

  public float getRadius() {
    return this.radius;
  }
}
