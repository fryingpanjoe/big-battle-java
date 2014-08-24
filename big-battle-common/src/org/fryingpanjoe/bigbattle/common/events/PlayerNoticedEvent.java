package org.fryingpanjoe.bigbattle.common.events;

import org.fryingpanjoe.bigbattle.common.game.Player;

public class PlayerNoticedEvent {

  public final Player player;

  public PlayerNoticedEvent(final Player player) {
    this.player = player;
  }
}
