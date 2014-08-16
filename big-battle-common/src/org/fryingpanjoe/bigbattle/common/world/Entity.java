package org.fryingpanjoe.bigbattle.common.world;

import org.lwjgl.util.vector.Vector2f;

public class Entity {

  public final int id;
  public Vector2f pos = new Vector2f();
  public Vector2f vel = new Vector2f();

  public Entity(final int id) {
    this.id = id;
  }
}
