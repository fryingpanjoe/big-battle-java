package org.fryingpanjoe.bigbattle.server.game;

import org.fryingpanjoe.bigbattle.common.game.Entity;

public class Noticer {

  private final Entity entity;
  private final float radius;

  public Noticer(final Entity entity, final float radius) {
    this.entity = entity;
    this.radius = radius;
  }

  public Entity getEntity() {
    return this.entity;
  }

  public float getRadius() {
    return this.radius;
  }
}
