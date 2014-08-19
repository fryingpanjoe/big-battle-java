package org.fryingpanjoe.bigbattle.common.events;

import org.fryingpanjoe.bigbattle.common.game.Player;

public class NoticePlayerEvent {

  public final Player player;

  public NoticePlayerEvent(final Player player) {
    this.player = player;
  }
}
