package org.fryingpanjoe.bigbattle.server.game;

import org.fryingpanjoe.bigbattle.common.game.Entity;

public class ServerEntity {

  private final Entity entity;

  public ServerEntity(final Entity entity) {
    this.entity = entity;
  }

  public Entity getEntity() {
    return this.entity;
  }
}
