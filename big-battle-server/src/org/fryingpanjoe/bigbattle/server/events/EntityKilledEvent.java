package org.fryingpanjoe.bigbattle.server.events;

import org.fryingpanjoe.bigbattle.server.game.ServerEntity;

public class EntityKilledEvent {

  public final ServerEntity entity;

  public EntityKilledEvent(final ServerEntity entity) {
    this.entity = entity;
  }
}
