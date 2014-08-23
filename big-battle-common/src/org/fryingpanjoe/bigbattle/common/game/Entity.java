package org.fryingpanjoe.bigbattle.common.game;

import org.lwjgl.util.vector.Vector2f;

public class Entity {

  private final int id;
  private final EntityDefinition def;
  private Vector2f pos = new Vector2f();
  private Vector2f vel = new Vector2f();
  private float rotation;

  public Entity(final int id,
                final EntityDefinition def,
                final float x,
                final float y,
                final float velX,
                final float velY,
                final float rotation) {
    this.id = id;
    this.def = def;
    this.pos.x = x;
    this.pos.y = y;
    this.vel.x = velX;
    this.vel.y = velY;
    this.rotation = rotation;
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

  public void setRotation(final float rotation) {
    this.rotation = rotation;
  }

  public Vector2f getPos() {
    return this.pos;
  }

  public Vector2f getVel() {
    return this.vel;
  }

  public float getRotation() {
    return this.rotation;
  }
}
