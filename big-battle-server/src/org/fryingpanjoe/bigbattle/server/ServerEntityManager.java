package org.fryingpanjoe.bigbattle.server;

import java.util.HashMap;
import java.util.Map;

import org.fryingpanjoe.bigbattle.server.game.ServerEntity;

public class ServerEntityManager {

  private final Map<Integer, ServerEntity> entities;

  public ServerEntityManager() {
    this.entities = new HashMap<>();
  }

  public Map<Integer, ServerEntity> getEntities() {
    return this.entities;
  }
}
