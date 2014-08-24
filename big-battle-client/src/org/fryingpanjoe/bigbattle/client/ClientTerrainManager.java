package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.common.terrain.TerrainGenerator;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainPatch;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainPatchLocation;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainPatches;
import org.json.JSONException;

public class ClientTerrainManager {

  private static final Logger LOG = Logger.getLogger(ClientTerrainManager.class.getName());

  private static final int PATCH_SIZE = 128;
  private static final float TILE_WORLD_SIZE = 1.f;

  private static class TerrainPatchWithLocation {

    public final TerrainPatch patch;
    public final TerrainPatchLocation location;

    public TerrainPatchWithLocation(final TerrainPatch patch, final TerrainPatchLocation location) {
      this.patch = patch;
      this.location = location;
    }
  }

  private final List<TerrainPatchWithLocation> patches;

  public ClientTerrainManager() {
    this.patches = new ArrayList<>();
  }

  public TerrainPatch getTerrainPatch(final float x, final float y) {
    final TerrainPatchLocation location = getTerrainPatchLocationAt(x, y);
    for (TerrainPatchWithLocation patch : this.patches) {
      if (patch.location.equals(location)) {
        return patch.patch;
      }
    }
    try {
      final TerrainPatch patch = TerrainPatches.getTerrainPatch(location);
      this.patches.add(new TerrainPatchWithLocation(patch, location));
      return patch;
    } catch (final JSONException | IOException e) {
      LOG.warning(
        String.format("Failed to load patch %d,%d: %s", location.x, location.y, e.getMessage()));
      final TerrainPatch patch = TerrainGenerator.generateRandomPatch(
        PATCH_SIZE, Arrays.asList(0), new Random());
      this.patches.add(new TerrainPatchWithLocation(patch, location));
      return patch;
    }
  }

  private static TerrainPatchLocation getTerrainPatchLocationAt(final float x, final float y) {
    return new TerrainPatchLocation(
      (int) (x / TILE_WORLD_SIZE) / PATCH_SIZE,
      (int) (y / TILE_WORLD_SIZE) / PATCH_SIZE);
  }
}
