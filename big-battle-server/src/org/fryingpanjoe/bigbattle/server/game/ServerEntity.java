package org.fryingpanjoe.bigbattle.server.game;

import org.fryingpanjoe.bigbattle.common.game.Entity;

public class ServerEntity {

  private final Entity entity;
  private ServerArea area;

  public ServerEntity(final Entity entity) {
    this.entity = entity;
    this.area = null;
  }

  public void setArea(final ServerArea area) {
    this.area = area;
  }

  public Entity getEntity() {
    return this.entity;
  }

  public ServerArea getArea() {
    return this.area;
  }
}
