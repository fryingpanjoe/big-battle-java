package org.fryingpanjoe.bigbattle.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fryingpanjoe.bigbattle.server.game.ServerEntity;

public class ServerEntityManager {

  private final Map<Integer, ServerEntity> entities;
  private final Quadtree<ServerEntity> quadtree;

  public ServerEntityManager() {
    this.entities = new HashMap<>();
    this.quadtree = new Quadtree<>();
  }

  public void updatePositions(final float dt) {
    for (final ServerEntity entity : this.entities.values()) {
      final float x = entity.getEntity().getX() + entity.getEntity().getVelocityX() * dt;
      final float y = entity.getEntity().getY() + entity.getEntity().getVelocityY() * dt;
      if (x != entity.getEntity().getX() && y != entity.getEntity().getY()) {
        entity.getEntity().setPosition(x, y);
        this.quadtree.remove(entity);
        this.quadtree.insert(entity, entity.getEntity().getX(), entity.getEntity().getY());
      }
    }
  }

  public void addEntity(final ServerEntity entity) {
    this.entities.put(entity.getEntity().getId(), entity);
    this.quadtree.insert(entity, entity.getEntity().getX(), entity.getEntity().getY());
  }

  public void removeEntityById(final int entityId) {
    final ServerEntity entity = this.entities.get(entityId);
    if (entity != null) {
      this.entities.remove(entity.getEntity().getId());
      this.quadtree.remove(entity);
    }
  }

  public ServerEntity getEntity(final int entityId) {
    return this.entities.get(entityId);
  }

  public List<ServerEntity> getEntitiesInSphere(final float x, final float y, final float r) {
    final List<ServerEntity> found = getEntitiesInAabb(x - r, y - r, x + r, y + r);
    final List<ServerEntity> result = new ArrayList<>(found.size());
    for (final ServerEntity entity : found) {
      final float ex = entity.getEntity().getX();
      final float ey = entity.getEntity().getY();
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
}
