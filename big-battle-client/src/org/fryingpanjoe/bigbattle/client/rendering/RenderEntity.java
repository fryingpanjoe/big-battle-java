package org.fryingpanjoe.bigbattle.client.rendering;

import org.fryingpanjoe.bigbattle.common.game.Entity;

public class RenderEntity {

  private final Entity entity;

  public RenderEntity(final Entity entity) {
    this.entity = entity;
  }

  public Entity getEntity() {
    return this.entity;
  }
}
