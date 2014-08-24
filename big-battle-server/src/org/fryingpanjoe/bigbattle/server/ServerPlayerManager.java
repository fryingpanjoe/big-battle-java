package org.fryingpanjoe.bigbattle.server;

import java.util.HashMap;
import java.util.Map;

import org.fryingpanjoe.bigbattle.common.game.PlayerInput;
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
      player.getServerEntity().getEntity().setRotation(player.getPlayerInput().getRotation());
      float speedFactor = 1.f;
      float velx = 0.f;
      float vely = 0.f;
      for (final PlayerInput.Action action : player.getPlayerInput().getActions()) {
        switch (action) {
          case Running:
            speedFactor = 2.f;
            break;

          case MovingNorth:
            velx += 0.5f;
            vely += -0.5f;
            break;

          case MovingSouth:
            velx += -0.5f;
            vely += 0.5f;
            break;

          case MovingWest:
            velx += -0.5f;
            vely += -0.5f;
            break;

          case MovingEast:
            velx += 0.5f;
            vely += 0.5f;
            break;

          case Attacking:
            // TODO implement
            break;
        }
      }
      if (velx != 0.f && vely != 0.f) {
        final float moveLength = (float) Math.sqrt((velx * velx) + (vely * vely));
        final float speed = speedFactor * player.getServerEntity().getEntity().getDef().getSpeed();
        velx *= speed / moveLength;
        vely *= speed / moveLength;
        player.getServerEntity().getEntity().setVelocity(velx, vely);
      }
    }
  }
}
