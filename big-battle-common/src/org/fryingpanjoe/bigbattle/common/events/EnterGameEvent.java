package org.fryingpanjoe.bigbattle.common.events;

public class EnterGameEvent {

  public final int clientId;
  public final int entityId;

  public EnterGameEvent(final int clientId, final int entityId) {
    this.clientId = clientId;
    this.entityId = entityId;
  }
}
