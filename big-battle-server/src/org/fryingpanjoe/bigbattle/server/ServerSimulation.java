package org.fryingpanjoe.bigbattle.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fryingpanjoe.bigbattle.server.events.EntityKilledEvent;
import org.fryingpanjoe.bigbattle.server.events.EntityMovedEvent;
import org.fryingpanjoe.bigbattle.server.events.EntitySpawnedEvent;
import org.fryingpanjoe.bigbattle.server.game.ServerEntity;

import com.google.common.eventbus.Subscribe;

public class ServerSimulation {

  private final Map<Integer, ServerEntity> entities;
  private final Quadtree<ServerEntity> quadtree;

  public ServerSimulation() {
    this.entities = new HashMap<>();
    this.quadtree = new Quadtree<>();
  }

  @Subscribe
  public void onEntitySpawnedEvent(final EntitySpawnedEvent event) {
    this.entities.put(event.entity.getEntity().getId(), event.entity);
    this.quadtree.insert(
      event.entity, event.entity.getEntity().getPos().x, event.entity.getEntity().getPos().y);
  }

  @Subscribe
  public void onEntityKilledEvent(final EntityKilledEvent event) {
    this.entities.remove(event.entity.getEntity().getId());
    this.quadtree.remove(event.entity);
  }

  @Subscribe
  public void onEntityMovedEvent(final EntityMovedEvent event) {
    this.quadtree.remove(event.entity);
    this.quadtree.insert(
      event.entity, event.entity.getEntity().getPos().x, event.entity.getEntity().getPos().y);
  }

  public List<ServerEntity> getEntitiesInSphere(final float x, final float y, final float r) {
    final List<ServerEntity> found = getEntitiesInAabb(x - r, y - r, x + r, y + r);
    final List<ServerEntity> result = new ArrayList<>(found.size());
    for (final ServerEntity entity : found) {
      final float ex = entity.getEntity().getPos().x;
      final float ey = entity.getEntity().getPos().y;
      final float er = entity.getEntity().getDef().getRadius();
      if (Intersecting.sphereSphere(x, y, r, ex, ey, er)) {
        result.add(entity);
      }
    }
    return result;
  }

  public List<ServerEntity> getEntitiesInAabb(final float x0,
                                              final float y0,
                                              final float x1,
                                              final float y1) {
    return this.quadtree.queryAabb(x0, y0, x1 - x0, y1 - y0);
  }

  public Map<Integer, ServerEntity> getEntities() {
    return this.entities;
  }
}
