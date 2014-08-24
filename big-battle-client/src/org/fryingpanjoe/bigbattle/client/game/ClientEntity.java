package org.fryingpanjoe.bigbattle.client.game;

import org.fryingpanjoe.bigbattle.common.game.Entity;

public class ClientEntity {

  private final Entity entity;

  public ClientEntity(final Entity entity) {
    this.entity = entity;
  }

  public Entity getEntity() {
    return this.entity;
  }
}
