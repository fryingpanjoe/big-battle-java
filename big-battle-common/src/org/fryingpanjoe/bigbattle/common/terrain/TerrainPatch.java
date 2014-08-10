package org.fryingpanjoe.bigbattle.common.terrain;

public class TerrainPatch {

  private final int size;
  private final int[] tiles;

  public TerrainPatch(final int size, final int[] tiles) {
    assert tiles.length == (size * size) : "bad tile array size";
    this.size = size;
    this.tiles = tiles;
  }

  public int getSize() {
    return this.size;
  }

  public int[] getTiles() {
    return this.tiles;
  }
}
