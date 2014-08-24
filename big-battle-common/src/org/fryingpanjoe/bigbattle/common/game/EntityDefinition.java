package org.fryingpanjoe.bigbattle.common.game;

import org.lwjgl.util.vector.Vector3f;

public class EntityDefinition {

  private final int id;
  private final float mass;
  private final float radius;
  private final Vector3f size;
  private final float speed;
  private final float rotationSpeed;

  public EntityDefinition(final int id,
                          final float mass,
                          final float radius,
                          final Vector3f size,
                          final float speed,
                          final float rotationSpeed) {
    this.id = id;
    this.mass = mass;
    this.radius = radius;
    this.size = size;
    this.speed = speed;
    this.rotationSpeed = rotationSpeed;
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

  public Vector3f getSize() {
    return this.size;
  }

  public float getSpeed() {
    return this.speed;
  }

  public float getRotationSpeed() {
    return this.rotationSpeed;
  }
}
