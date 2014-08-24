package org.fryingpanjoe.bigbattle.common.game;

public class Player {

  private final int clientId;
  private final int entityId;

  public Player(final int clientId, final int entityId) {
    this.clientId = clientId;
    this.entityId = entityId;
  }

  public int getClientId() {
    return this.clientId;
  }

  public int getEntityId() {
    return this.entityId;
  }
}
