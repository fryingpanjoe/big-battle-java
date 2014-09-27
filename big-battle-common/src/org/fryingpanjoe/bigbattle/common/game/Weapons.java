package org.fryingpanjoe.bigbattle.common.game;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class Weapons {

  public enum WeaponId {
    Fist,
    Stick,
  }

  public static final Weapon FIST = new Weapon(
    WeaponId.Fist.ordinal(), // id
    1.f, // damage
    1.f, // range
    1.f // delay
  );

  public static final Weapon STICK = new Weapon(
    WeaponId.Stick.ordinal(), // id
    10.f, // damage
    1.5f, // range
    1.f // delay
  );

  private static final Map<WeaponId, Weapon> ALL = ImmutableMap.of(
    WeaponId.Stick, STICK,
    WeaponId.Fist, FIST);

  private Weapons() {
  }

  public static Weapon getWeapon(final WeaponId id) {
    return ALL.get(id);
  }

  public static Weapon getWeapon(final int id) {
    assert id < WeaponId.values().length : "invalid weapon id";
    return ALL.get(WeaponId.values()[id]);
  }
}
