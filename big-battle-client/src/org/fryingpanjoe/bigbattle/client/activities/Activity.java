package org.fryingpanjoe.bigbattle.client.activities;

public interface Activity {

  boolean update(final long deltaTime);

  void key(final int key, final char character, final boolean down);

  void mouseButton(final int button, final boolean down);

  void mouseWheel(final int delta);

  void mouseMove(final int x, final int y, final int dx, final int dy);
}
