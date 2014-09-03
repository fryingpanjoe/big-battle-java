package org.fryingpanjoe.bigbattle.client;

import org.fryingpanjoe.bigbattle.common.game.PlayerInput;
import org.lwjgl.input.Keyboard;

public class Keybinding {

  private static final int KEY_COUNT = Keyboard.KEYBOARD_SIZE + 20;

  private static final int MOUSE_START = Keyboard.KEYBOARD_SIZE;
  public static final int MWHEELUP = MOUSE_START + 1;
  public static final int MWHEELDOWN = MOUSE_START + 2;
  public static final int MOUSE0 = MOUSE_START + 3;
  public static final int MOUSE1 = MOUSE_START + 4;
  public static final int MOUSE2 = MOUSE_START + 5;
  public static final int MOUSE3 = MOUSE_START + 6;
  public static final int MOUSE4 = MOUSE_START + 7;
  public static final int MOUSE5 = MOUSE_START + 8;
  public static final int MOUSE6 = MOUSE_START + 9;
  public static final int MOUSE7 = MOUSE_START + 10;
  public static final int MOUSE8 = MOUSE_START + 11;
  public static final int MOUSE9 = MOUSE_START + 12;
  public static final int MOUSE10 = MOUSE_START + 13;
  public static final int MOUSE11 = MOUSE_START + 14;
  public static final int MOUSE12 = MOUSE_START + 15;
  public static final int MOUSE13 = MOUSE_START + 16;
  public static final int MOUSE14 = MOUSE_START + 17;
  public static final int MOUSE15 = MOUSE_START + 18;
  public static final int MOUSE16 = MOUSE_START + 19;

  private static final String MWHEELUP_NAME = "MWHEELUP";
  private static final String MWHEELDOWN_NAME = "MWHEELDOWN";

  private final PlayerInput.Action[] binding;

  public Keybinding() {
    this.binding = new PlayerInput.Action[KEY_COUNT];
  }

  public static int getKeyForName(final String name) {
    final int key = Keyboard.getKeyIndex(name.toUpperCase());
    if (key != Keyboard.KEY_NONE) {
      return key;
    } else if (MWHEELUP_NAME.equalsIgnoreCase(name)) {
      return MWHEELUP;
    } else if (MWHEELDOWN_NAME.equalsIgnoreCase(name)) {
      return MWHEELDOWN;
    } else if (name.toUpperCase().startsWith("MOUSE")) {
      return MOUSE0 + Integer.parseInt(name.substring("MOUSE".length()));
    } else {
      return Keyboard.KEY_NONE;
    }
  }

  public static String getNameForKey(final int key) {
    if (key < Keyboard.KEYBOARD_SIZE) {
      return Keyboard.getKeyName(key);
    } else if (key == MWHEELUP) {
      return MWHEELUP_NAME;
    } else if (key == MWHEELDOWN) {
      return MWHEELDOWN_NAME;
    } else if (key >= MOUSE0 && key <= MOUSE16) {
      return String.format("MOUSE%d", key - MOUSE0);
    } else {
      return "";
    }
  }

  public void bind(final int key, final PlayerInput.Action action) {
    assert key >= 0 && key < KEY_COUNT : "invalid key";
    this.binding[key] = action;
  }

  public void unbind(final int key) {
    assert key >= 0 && key < KEY_COUNT : "invalid key";
    this.binding[key] = null;
  }

  public PlayerInput.Action get(final int key) {
    assert key >= 0 && key < KEY_COUNT : "invalid key";
    return this.binding[key];
  }
}
