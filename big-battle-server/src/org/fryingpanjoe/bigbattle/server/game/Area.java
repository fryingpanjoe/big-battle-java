package org.fryingpanjoe.bigbattle.server.game;

import org.lwjgl.util.vector.Vector2f;

public class Area {

  private final Vector2f pos;
  private final Vector2f size;

  public Area() {
    this.pos = new Vector2f();
    this.size = new Vector2f();
  }
}
