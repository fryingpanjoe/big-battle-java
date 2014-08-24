package org.fryingpanjoe.bigbattle.common.game;

public class Entity {

  public static final byte POSITION_BIT    = 0b00000001;
  public static final byte VELOCITY_BIT    = 0b00000010;
  public static final byte ROTATION_BIT    = 0b00000100;

  private final int id;
  private final EntityDefinition def;

  private byte updateBits;

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
    this.updateBits = 0;
    this.x = x;
    this.y = y;
    this.velx = velx;
    this.vely = vely;
    this.rotation = rotation;
  }

  public boolean checkUpdateBits(final byte bits) {
    return (this.updateBits & bits) == bits;
  }

  public void resetUpdateBits(final byte bits) {
    this.updateBits &= ~bits;
  }

  public void clearUpdateBits() {
    this.updateBits = 0;
  }

  public void setUpdateBits(final byte bits) {
    this.updateBits = bits;
  }

  public void setPosition(final float x, final float y) {
    this.x = x;
    this.y = y;
    this.updateBits |= POSITION_BIT;
  }

  public void setVelocity(final float velx, final float vely) {
    this.velx = velx;
    this.vely = vely;
    this.updateBits |= VELOCITY_BIT;
  }

  public void setRotation(final float rotation) {
    this.rotation = rotation;
    this.updateBits |= ROTATION_BIT;
  }

  public int getId() {
    return this.id;
  }

  public EntityDefinition getDef() {
    return this.def;
  }

  public byte getUpdateBits() {
    return this.updateBits;
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
