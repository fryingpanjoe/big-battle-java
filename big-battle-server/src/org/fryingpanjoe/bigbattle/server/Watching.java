package org.fryingpanjoe.bigbattle.server;

import java.util.HashSet;
import java.util.Set;

public class Watching {

  private final Set<Integer> entities;
  private final Set<Integer> players;

  public Watching() {
    this.entities = new HashSet<>();
    this.players = new HashSet<>();
  }

  public void watchEntity(final int entityId, final boolean watch) {
    if (watch) {
      this.entities.add(entityId);
    } else {
      this.entities.remove(entityId);
    }
  }

  public void watchPlayer(final int playerId, final boolean watch) {
    if (watch) {
      this.players.add(playerId);
    } else {
      this.players.remove(playerId);
    }
  }

  public Set<Integer> getWatchedEntities() {
    return this.entities;
  }

  public Set<Integer> getWatchedPlayers() {
    return this.players;
  }
}
