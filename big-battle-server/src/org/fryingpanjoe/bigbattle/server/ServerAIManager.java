package org.fryingpanjoe.bigbattle.server;

import java.util.HashMap;
import java.util.Map;

import org.fryingpanjoe.bigbattle.server.game.ServerAI;

public class ServerAIManager {

  private final Map<Integer, ServerAI> ais;

  public ServerAIManager() {
    this.ais = new HashMap<>();
  }

  public void addAI(final ServerAI ai) {
    this.ais.put(ai.getServerEntity().getEntity().getId(), ai);
  }

  public void removeAIByEntityId(final int entityId) {
    this.ais.remove(entityId);
  }

  public ServerAI getAIByEntityId(final int entityId) {
    return this.ais.get(entityId);
  }

  public Map<Integer, ServerAI> getAIs() {
    return this.ais;
  }

  public void updateAI(final float deltaTime) {
    for (final ServerAI ai : this.ais.values()) {
      ai.update(deltaTime);
    }
  }
}
