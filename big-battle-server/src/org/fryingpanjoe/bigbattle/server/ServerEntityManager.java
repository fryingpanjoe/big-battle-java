package org.fryingpanjoe.bigbattle.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fryingpanjoe.bigbattle.common.game.Entity;
import org.fryingpanjoe.bigbattle.server.game.ServerEntity;
import org.lwjgl.util.vector.Vector2f;

public class ServerEntityManager {

  private final Map<Integer, ServerEntity> entities;
  private final Quadtree<ServerEntity> quadtree;

  public ServerEntityManager() {
    this.entities = new HashMap<>();
    this.quadtree = new Quadtree<>();
  }

  public void update(final float dt) {
    for (final ServerEntity entity : this.entities.values()) {
      if (entity.getEntity().getState() == Entity.State.Dead) {
        continue;
      }
      // move
      final float x = entity.getEntity().getX() + entity.getEntity().getVelocityX() * dt;
      final float y = entity.getEntity().getY() + entity.getEntity().getVelocityY() * dt;
      if (x != entity.getEntity().getX() || y != entity.getEntity().getY()) {
        entity.getEntity().setPosition(x, y);
        this.quadtree.remove(entity);
        this.quadtree.insert(entity, entity.getEntity().getX(), entity.getEntity().getY());
      }
      // attack
      entity.getEntity().setWeaponTimer(Math.max(0.f, entity.getEntity().getWeaponTimer() - dt));
      if (entity.getEntity().getState() == Entity.State.Attacking) {
        if (entity.getEntity().getWeaponTimer() <= 0.f) {
          // get possible targets
          final List<ServerEntity> targets = getEntitiesInSphere(
            entity.getEntity().getX(),
            entity.getEntity().getY(),
            entity.getEntity().getWeapon().getRange());
          final Vector2f attackDirection = new Vector2f(
            (float) Math.cos(entity.getEntity().getRotation()),
            (float) Math.sin(entity.getEntity().getRotation()));
          for (final ServerEntity target : targets) {
            if (target == entity) {
              // don't attack yourself
              continue;
            }
            // check if target is in front of attacker
            final Vector2f toTarget = new Vector2f(
              target.getEntity().getX() - entity.getEntity().getX(),
              target.getEntity().getY() - entity.getEntity().getY());
            final float dot = Vector2f.dot(attackDirection, toTarget);
            if (dot >= 0.f) {
              // hit
              target.getEntity().setHealth(
                target.getEntity().getHealth() - entity.getEntity().getWeapon().getDamage());
              if (target.getEntity().getHealth() <= 0.f) {
                target.getEntity().setState(Entity.State.Dead);
              }
            }
          }
          entity.getEntity().setWeaponTimer(entity.getEntity().getWeapon().getDelay());
        }
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
      final float er = entity.getEntity().getDefinition().getRadius();
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
