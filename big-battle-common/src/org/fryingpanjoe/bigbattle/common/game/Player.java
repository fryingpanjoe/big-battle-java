package org.fryingpanjoe.bigbattle.common.game;

public class Player {

  private final int id;
  private final int clientId;
  private final int entityId;
  private final String name;

  public Player(final int id, final int clientId, final int entityId, final String name) {
    this.id = id;
    this.clientId = clientId;
    this.entityId = entityId;
    this.name = name;
  }

  public int getId() {
    return this.id;
  }

  public int getClientId() {
    return this.clientId;
  }

  public int getEntityId() {
    return this.entityId;
  }

  public String getName() {
    return this.name;
  }
}
