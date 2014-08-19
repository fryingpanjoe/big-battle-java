package org.fryingpanjoe.bigbattle.client.game;

import org.fryingpanjoe.bigbattle.common.game.Player;

public class ClientPlayer {

  private final Player player;
  private final ClientEntity entity;

  public ClientPlayer(final Player player, final ClientEntity entity) {
    this.player = player;
    this.entity = entity;
  }

  public Player getPlayer() {
    return this.player;
  }

  public ClientEntity getClientEntity() {
    return this.entity;
  }
}
