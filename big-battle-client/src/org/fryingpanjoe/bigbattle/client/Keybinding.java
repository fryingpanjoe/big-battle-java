package org.fryingpanjoe.bigbattle.client;

import org.fryingpanjoe.bigbattle.common.game.PlayerInput;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Keybinding {

  private static final int KEY_COUNT = Keyboard.KEYBOARD_SIZE + Mouse.getButtonCount();

  public static final int MOUSE1 = Keyboard.KEYBOARD_SIZE;
  public static final int MOUSE2 = MOUSE1 + 1;
  public static final int MOUSE3 = MOUSE1 + 2;
  public static final int MOUSE_WHEELUP = MOUSE1 + 3;
  public static final int MOUSE_WHEELDOWN = MOUSE1 + 4;

  private final PlayerInput.Action[] binding;

  public Keybinding() {
    this.binding = new PlayerInput.Action[KEY_COUNT];
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
