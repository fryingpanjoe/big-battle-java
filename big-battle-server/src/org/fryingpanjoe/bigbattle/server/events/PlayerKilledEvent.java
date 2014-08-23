package org.fryingpanjoe.bigbattle.server.events;

public class PlayerKilledEvent {

  public final int playerId;

  public PlayerKilledEvent(final int playerId) {
    this.playerId = playerId;
  }
}
