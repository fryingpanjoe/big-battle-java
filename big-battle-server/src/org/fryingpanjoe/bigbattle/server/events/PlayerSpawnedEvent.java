package org.fryingpanjoe.bigbattle.server.events;

import org.fryingpanjoe.bigbattle.server.game.ServerPlayer;

public class PlayerSpawnedEvent {

  public final ServerPlayer player;

  public PlayerSpawnedEvent(final ServerPlayer player) {
    this.player = player;
  }
}
