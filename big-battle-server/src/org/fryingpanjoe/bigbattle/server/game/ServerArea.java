package org.fryingpanjoe.bigbattle.server.game;

import java.util.HashSet;
import java.util.Set;

import org.fryingpanjoe.bigbattle.server.Intersecting;
import org.lwjgl.util.vector.Vector2f;

public class ServerArea {

  private final Vector2f pos;
  private final Vector2f size;
  private final Set<ServerEntity> entities;

  public ServerArea(final Vector2f pos, final Vector2f size) {
    this.pos = pos;
    this.size = size;
    this.entities = new HashSet<>();
  }

  public void entityEntered(final ServerEntity entity) {
    this.entities.add(entity);
  }

  public void entityLeft(final ServerEntity entity) {
    this.entities.remove(entity);
  }

  public boolean hasEntity(final ServerEntity entity) {
    return this.entities.contains(entity);
  }

  public boolean intersectPoint(final float x, final float y) {
    return Intersecting.pointAabb(
      x, y, this.pos.x, this.pos.y, this.pos.x + this.size.x, this.pos.y + this.size.y);
  }

  public Vector2f getPos() {
    return this.pos;
  }

  public Vector2f getSize() {
    return this.size;
  }
}
