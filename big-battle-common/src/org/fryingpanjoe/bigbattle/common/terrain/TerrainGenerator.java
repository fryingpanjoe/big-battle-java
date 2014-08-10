package org.fryingpanjoe.bigbattle.common.terrain;

import java.util.List;
import java.util.Random;

public class TerrainGenerator {

  public static TerrainPatch generateRandomPatch(
      final int size,
      final List<Integer> types,
      final Random random) {
    final int[] tiles = new int[size * size];
    for (int i = 0; i < size * size; ++i) {
      tiles[i] = types.get(random.nextInt(types.size()));
    }
    return new TerrainPatch(size, tiles);
  }
}
