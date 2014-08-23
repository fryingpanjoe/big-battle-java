package org.fryingpanjoe.bigbattle.server.events;

import org.fryingpanjoe.bigbattle.server.game.ServerEntity;

public class EntitySpawnedEvent {

  public final ServerEntity entity;

  public EntitySpawnedEvent(final ServerEntity entity) {
    this.entity = entity;
  }
}
