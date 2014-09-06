package org.fryingpanjoe.bigbattle.common.game;

import java.util.EnumSet;

public class Entity {

  public enum UpdateFlag {
    Position,
    Velocity,
    Rotation,
  }

  private final int id;
  private final EntityDefinition def;

  private final EnumSet<UpdateFlag> updateFlags;

  private float x;
  private float y;
  private float velx;
  private float vely;
  private float rotation;

  public Entity(final int id,
                final EntityDefinition def,
                final float x,
                final float y,
                final float velx,
                final float vely,
                final float rotation) {
    this.id = id;
    this.def = def;
    this.updateFlags = EnumSet.noneOf(UpdateFlag.class);
    this.x = x;
    this.y = y;
    this.velx = velx;
    this.vely = vely;
    this.rotation = rotation;
  }

  public void setPosition(final float x, final float y) {
    this.x = x;
    this.y = y;
    this.updateFlags.add(UpdateFlag.Position);
  }

  public void setVelocity(final float velx, final float vely) {
    this.velx = velx;
    this.vely = vely;
    this.updateFlags.add(UpdateFlag.Velocity);
  }

  public void setRotation(final float rotation) {
    this.rotation = rotation;
    this.updateFlags.add(UpdateFlag.Rotation);
  }

  public int getId() {
    return this.id;
  }

  public EntityDefinition getDef() {
    return this.def;
  }

  public EnumSet<UpdateFlag> getUpdateFlags() {
    return this.updateFlags;
  }

  public float getX() {
    return this.x;
  }

  public float getY() {
    return this.y;
  }

  public float getVelocityX() {
    return this.velx;
  }

  public float getVelocityY() {
    return this.vely;
  }

  public float getRotation() {
    return this.rotation;
  }
}
