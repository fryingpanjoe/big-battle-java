package org.fryingpanjoe.bigbattle.server.game;

import org.fryingpanjoe.bigbattle.common.game.Player;
import org.fryingpanjoe.bigbattle.common.game.PlayerInput;

public class ServerPlayer {

  private final Player player;
  private final ServerEntity entity;
  private final ServerNotice notice;
  private PlayerInput playerInput;

  public ServerPlayer(final Player player, final ServerEntity entity, final ServerNotice notice) {
    this.player = player;
    this.entity = entity;
    this.notice = notice;
  }

  public void setPlayerInput(final PlayerInput playerInput) {
    this.playerInput = playerInput;
  }

  public Player getPlayer() {
    return this.player;
  }

  public ServerEntity getServerEntity() {
    return this.entity;
  }

  public ServerNotice getNotice() {
    return this.notice;
  }

  public PlayerInput getPlayerInput() {
    return this.playerInput;
  }
}
