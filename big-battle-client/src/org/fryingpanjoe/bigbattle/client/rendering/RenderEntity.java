package org.fryingpanjoe.bigbattle.client.rendering;

import org.fryingpanjoe.bigbattle.common.game.Entity;
import org.lwjgl.util.vector.Vector3f;

public class RenderEntity {

  private final Entity entity;
  private final Vector3f size;

  public RenderEntity(final Entity entity) {
    this.entity = entity;
    this.size = new Vector3f(
      this.entity.getDef().getRadius(),
      this.entity.getDef().getRadius(),
      this.entity.getDef().getRadius());
  }

  public void setSize(final float x, final float y, final float z) {
    this.size.x = x;
    this.size.y = y;
    this.size.z = z;
  }

  public Vector3f getSize() {
    return this.size;
  }

  public Entity getEntity() {
    return this.entity;
  }
}
