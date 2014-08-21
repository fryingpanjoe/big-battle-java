package org.fryingpanjoe.bigbattle.common.events;

public class EnterGameEvent {

  public final int playerId;

  public EnterGameEvent(final int playerId) {
    this.playerId = playerId;
  }
}
