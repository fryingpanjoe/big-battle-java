package org.fryingpanjoe.bigbattle.server.game;

import org.fryingpanjoe.bigbattle.common.game.ai.AI;

public class ServerAI {

  private final AI ai;
  private final ServerEntity entity;
  private final ServerNotice notice;

  public ServerAI(final AI ai, final ServerEntity entity, final ServerNotice notice) {
    this.ai = ai;
    this.entity = entity;
    this.notice = notice;
  }

  public void update(final float dt) {
    this.ai.think(dt);
  }

  public AI getAIController() {
    return this.ai;
  }

  public ServerEntity getServerEntity() {
    return this.entity;
  }

  public ServerNotice getNotice() {
    return this.notice;
  }
}
