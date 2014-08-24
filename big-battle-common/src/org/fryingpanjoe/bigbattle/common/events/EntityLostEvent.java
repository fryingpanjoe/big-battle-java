package org.fryingpanjoe.bigbattle.common.events;

public class EntityLostEvent {

  public final int entityId;

  public EntityLostEvent(final int entityId) {
    this.entityId = entityId;
  }
}
