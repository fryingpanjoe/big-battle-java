package org.fryingpanjoe.bigbattle.client;

import java.util.HashMap;
import java.util.Map;

import org.fryingpanjoe.bigbattle.client.game.ClientPlayer;
import org.fryingpanjoe.bigbattle.common.events.LostPlayerEvent;
import org.fryingpanjoe.bigbattle.common.events.NoticePlayerEvent;

import com.google.common.eventbus.Subscribe;

public class ClientPlayerManager {

  private final ClientEntityManager entityManager;
  private final Map<Integer, ClientPlayer> players;

  public ClientPlayerManager(final ClientEntityManager entityManager) {
    this.entityManager = entityManager;
    this.players = new HashMap<>();
  }

  public Map<Integer, ClientPlayer> getPlayers() {
    return this.players;
  }

  @Subscribe
  public void onNoticePlayerEvent(final NoticePlayerEvent event) {
    this.players.put(
      event.player.getId(),
      new ClientPlayer(
        event.player, this.entityManager.getEntities().get(event.player.getEntityId())));
  }

  @Subscribe
  public void onLostPlayerEvent(final LostPlayerEvent event) {
    this.players.remove(event.id);
  }
}
