package org.fryingpanjoe.bigbattle.server.game;

import org.fryingpanjoe.bigbattle.common.game.Entity;

public class ServerNotice {

  private final Entity entity;
  private final float noticeRadius;

  public ServerNotice(final Entity entity, final float noticeRadius) {
    this.entity = entity;
    this.noticeRadius = noticeRadius;
  }

  public Entity getEntity() {
    return this.entity;
  }

  public float getNoticeRadius() {
    return this.noticeRadius;
  }
}
