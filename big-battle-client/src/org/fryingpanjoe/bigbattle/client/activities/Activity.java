package org.fryingpanjoe.bigbattle.client.activities;

public interface Activity {

  boolean update(final float timeDelta);

  void draw();

  void key(final int key, final char character, final boolean down);

  void mouseMove(final int x, final int y, final int dx, final int dy);
}
