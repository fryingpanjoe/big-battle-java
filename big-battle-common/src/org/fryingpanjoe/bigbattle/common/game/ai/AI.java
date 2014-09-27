package org.fryingpanjoe.bigbattle.common.game.ai;

import org.fryingpanjoe.bigbattle.common.game.Entity;
import org.fryingpanjoe.bigbattle.common.game.ai.behaviors.Behavior;
import org.fryingpanjoe.bigbattle.common.game.ai.behaviors.Roam;

public class AI {

  private final Entity entity;

  private Behavior behavior;

  public AI(final Entity entity) {
    this.entity = entity;
    this.behavior = new Roam(entity, 5.f, 5.f);
  }

  public void think(final float deltaTime) {
    if (this.entity.getState() != Entity.State.Dead) {
      this.behavior.apply(deltaTime);
    }
  }
}
