package org.fryingpanjoe.bigbattle.server.events;

import org.fryingpanjoe.bigbattle.server.game.ServerEntity;

public class EntityMovedEvent {

  public final ServerEntity entity;

  public EntityMovedEvent(final ServerEntity entity) {
    this.entity = entity;
  }
}
