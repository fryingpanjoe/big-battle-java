package org.fryingpanjoe.bigbattle.server.game;

import org.fryingpanjoe.bigbattle.common.game.Player;

public class ServerPlayer {

  private final Player player;
  private final ServerEntity entity;

  public ServerPlayer(final Player player, final ServerEntity entity) {
    this.player = player;
    this.entity = entity;
  }

  public Player getPlayer() {
    return this.player;
  }

  public ServerEntity getServerEntity() {
    return this.entity;
  }
}
