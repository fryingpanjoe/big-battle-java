package org.fryingpanjoe.bigbattle.server;

import java.util.HashMap;
import java.util.Map;

import org.fryingpanjoe.bigbattle.common.game.PlayerInputController;
import org.fryingpanjoe.bigbattle.server.game.ServerPlayer;

public class ServerPlayerManager {

  private final Map<Integer, ServerPlayer> players;

  public ServerPlayerManager() {
    this.players = new HashMap<>();
  }

  public void addPlayer(final ServerPlayer player) {
    this.players.put(player.getPlayer().getClientId(), player);
  }

  public void removePlayerByClientId(final int clientId) {
    this.players.remove(clientId);
  }

  public ServerPlayer getPlayerByClientId(final int clientId) {
    return this.players.get(clientId);
  }

  public Map<Integer, ServerPlayer> getPlayers() {
    return this.players;
  }

  public void updatePlayerInput() {
    for (final ServerPlayer player : this.players.values()) {
      if (player.hasPlayerInput()) {
        PlayerInputController.respondToPlayerInput(
          player.getServerEntity().getEntity(),
          player.getPlayerInput());
      }
    }
  }
}
