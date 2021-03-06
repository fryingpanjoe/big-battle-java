package org.fryingpanjoe.bigbattle.common.game;

import java.util.EnumSet;

public class Entity {

  public enum UpdateFlag {
    Position,
    Velocity,
    Rotation,
    State,
    Health,
    Weapon,
    WeaponTimer,
  }

  public enum State {
    Dead,
    Idle,
    Attacking,
  }

  private final int id;
  private final EntityDefinition definition;

  private final EnumSet<UpdateFlag> updateFlags;

  private float x;
  private float y;
  private float velx;
  private float vely;
  private float rotation;
  private State state;
  private float health;
  private Weapon weapon;
  private float weaponTimer;

  public Entity(final int id,
                final EntityDefinition definition,
                final float x,
                final float y,
                final float velx,
                final float vely,
                final float rotation,
                final State state,
                final float health,
                final Weapon weapon,
                final float weaponTimer) {
    this.id = id;
    this.definition = definition;
    this.updateFlags = EnumSet.noneOf(UpdateFlag.class);
    this.x = x;
    this.y = y;
    this.velx = velx;
    this.vely = vely;
    this.rotation = rotation;
    this.state = state;
    this.health = health;
    this.weapon = weapon;
    this.weaponTimer = weaponTimer;
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

  public void setState(final State state) {
    this.state = state;
    this.updateFlags.add(UpdateFlag.State);
  }

  public void setHealth(final float health) {
    this.health = health;
    this.updateFlags.add(UpdateFlag.Health);
  }

  public void setWeapon(final Weapon weapon) {
    this.weapon = weapon;
    this.updateFlags.add(UpdateFlag.Weapon);
  }

  public void setWeaponTimer(final float weaponTimer) {
    this.weaponTimer = weaponTimer;
    this.updateFlags.add(UpdateFlag.WeaponTimer);
  }

  public int getId() {
    return this.id;
  }

  public EntityDefinition getDefinition() {
    return this.definition;
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

  public State getState() {
    return this.state;
  }

  public float getHealth() {
    return this.health;
  }

  public Weapon getWeapon() {
    return this.weapon;
  }

  public float getWeaponTimer() {
    return this.weaponTimer;
  }
}
