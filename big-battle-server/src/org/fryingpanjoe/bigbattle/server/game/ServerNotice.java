package org.fryingpanjoe.bigbattle.server.game;

import java.util.HashSet;
import java.util.Set;

public class ServerNotice {

  private final ServerEntity entity;
  private final float range;
  private final Set<Integer> noticed;
  private final Set<Integer> lost;

  public ServerNotice(final ServerEntity entity, final float range) {
    this.entity = entity;
    this.range = range;
    this.noticed = new HashSet<>();
    this.lost = new HashSet<>();
  }

  public void entityNoticed(final int entityId) {
    this.noticed.add(entityId);
  }

  public void entityLost(final int entityId) {
    this.noticed.remove(entityId);
    this.lost.add(entityId);
  }

  public boolean isNoticed(final int entityId) {
    return this.noticed.contains(entityId);
  }

  public void clearLost() {
    this.lost.clear();
  }

  public ServerEntity getEntity() {
    return this.entity;
  }

  public float getRange() {
    return this.range;
  }

  public Set<Integer> getNoticed() {
    return this.noticed;
  }

  public Set<Integer> getLost() {
    return this.lost;
  }
}
