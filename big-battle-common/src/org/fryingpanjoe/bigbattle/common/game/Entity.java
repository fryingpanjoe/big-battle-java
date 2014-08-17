package org.fryingpanjoe.bigbattle.common.game;

import org.lwjgl.util.vector.Vector2f;

public class Entity {

  private final int id;
  private final EntityDefinition def;
  private Vector2f pos = new Vector2f();
  private Vector2f vel = new Vector2f();
  private Vector2f acc = new Vector2f();
  private Vector2f force = new Vector2f();
  private float rotation;

  public Entity(final int id,
                final EntityDefinition def,
                final float posX,
                final float posY,
                final float velX,
                final float velY,
                final float accX,
                final float accY,
                final float forceX,
                final float forceY,
                final float rotation) {
    this.id = id;
    this.def = def;
    this.pos.x = posX;
    this.pos.y = posY;
    this.vel.x = velX;
    this.vel.y = velY;
    this.acc.x = accX;
    this.acc.y = accY;
    this.force.x = forceX;
    this.force.y = forceY;
    this.rotation = rotation;
  }

  public void resetForce() {
    this.force.x = 0.f;
    this.force.y = 0.f;
  }

  public void applyForce(final float x, final float y) {
    this.force.x += x;
    this.force.y += y;
  }

  public void applyImpulse(final float x, final float y) {
    this.force.x += x * this.def.getMass();
    this.force.y += y * this.def.getMass();
  }

  public int getId() {
    return this.id;
  }

  public EntityDefinition getDef() {
    return this.def;
  }

  public void setPos(final float x, final float y) {
    this.pos.x = x;
    this.pos.y = y;
  }

  public void setVel(final float x, final float y) {
    this.vel.x = x;
    this.vel.y = y;
  }

  public void setAcc(final float x, final float y) {
    this.acc.x = x;
    this.acc.y = y;
  }

  public void setForce(final float x, final float y) {
    this.force.x = x;
    this.force.y = y;
  }

  public void setRotation(final float rotation) {
    this.rotation = rotation;
  }

  public Vector2f getPos() {
    return this.pos;
  }

  public Vector2f getVel() {
    return this.vel;
  }

  public Vector2f getAcc() {
    return this.acc;
  }

  public Vector2f getForce() {
    return this.force;
  }

  public float getRotation() {
    return this.rotation;
  }
}
