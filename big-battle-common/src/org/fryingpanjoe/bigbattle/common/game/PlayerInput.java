package org.fryingpanjoe.bigbattle.common.game;

import java.util.EnumSet;

public class PlayerInput {

  public enum Action {
    MovingNorth,
    MovingSouth,
    MovingEast,
    MovingWest,
    Running,
    Attacking,
  }

  private EnumSet<Action> actions;
  private float rotation;

  public PlayerInput() {
    this(EnumSet.noneOf(Action.class), 0.f);
  }

  public PlayerInput(final EnumSet<Action> actions, final float rotation) {
    this.actions = actions;
    this.rotation = rotation;
  }

  public void setAction(final Action action, final boolean on) {
    if (on) {
      this.actions.add(action);
    } else {
      this.actions.remove(action);
    }
  }

  public void setActions(final EnumSet<Action> actions) {
    this.actions = actions;
  }

  public void setRotation(final float rotation) {
    this.rotation = rotation;
  }

  public EnumSet<Action> getActions() {
    return this.actions;
  }

  public float getRotation() {
    return this.rotation;
  }
}
