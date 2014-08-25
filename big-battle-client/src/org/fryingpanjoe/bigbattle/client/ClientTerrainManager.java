package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.common.Constants;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainGenerator;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainPatch;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainPatchLocation;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainPatches;
import org.json.JSONException;

public class ClientTerrainManager {

  private static final Logger LOG = Logger.getLogger(ClientTerrainManager.class.getName());

  private final Map<TerrainPatchLocation, TerrainPatch> patches;

  public ClientTerrainManager() {
    this.patches = new HashMap<>();
  }

  public TerrainPatch getTerrainPatch(final TerrainPatchLocation location) {
    TerrainPatch patch = this.patches.get(location);
    if (patch == null) {
      try {
        patch = TerrainPatches.getTerrainPatch(location);
      } catch (final JSONException | IOException e) {
        LOG.warning(
          String.format("Failed to load patch %d,%d: %s", location.x, location.y, e.getMessage()));
        patch = TerrainGenerator.generateRandomPatch(
          Constants.AREA_SIZE_IN_TILES, Arrays.asList(0), new Random());
      }
    }
    this.patches.put(location, patch);
    return patch;
  }

  public static TerrainPatchLocation getTerrainPatchLocation(final float x, final float y) {
    return new TerrainPatchLocation(
      (int) (x / Constants.TILE_SIZE_IN_UNITS) / Constants.AREA_SIZE_IN_TILES,
      (int) (y / Constants.TILE_SIZE_IN_UNITS) / Constants.AREA_SIZE_IN_TILES);
  }
}
