package org.fryingpanjoe.bigbattle.common.events;

import org.fryingpanjoe.bigbattle.common.game.Entity;

public class EntityNoticedEvent {

  public final Entity entity;

  public EntityNoticedEvent(final Entity entity) {
    this.entity = entity;
  }
}
