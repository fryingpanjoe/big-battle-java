package org.fryingpanjoe.bigbattle.common.events;

import org.fryingpanjoe.bigbattle.common.game.Entity;

public class NoticeEntityEvent {

  public final Entity entity;

  public NoticeEntityEvent(final Entity entity) {
    this.entity = entity;
  }
}
