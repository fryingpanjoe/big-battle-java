package org.fryingpanjoe.bigbattle.server.events;

import org.fryingpanjoe.bigbattle.common.game.PlayerInput;

public class ServerPlayerInputEvent {

  public final int clientId;
  public final PlayerInput playerInput;

  public ServerPlayerInputEvent(final int clientId, final PlayerInput playerInput) {
    this.clientId = clientId;
    this.playerInput = playerInput;
  }
}
