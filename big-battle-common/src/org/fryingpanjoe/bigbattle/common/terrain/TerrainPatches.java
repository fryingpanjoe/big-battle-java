package org.fryingpanjoe.bigbattle.common.terrain;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.fryingpanjoe.bigbattle.common.io.Files;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TerrainPatches {

  private TerrainPatches() {
  }

  public static TerrainPatch getTerrainPatch(final TerrainPatchLocation location)
      throws JSONException, IOException {
    return getTerrainPatch(String.format("%d_%d.patch", location.x, location.y));
  }

  public static TerrainPatch getTerrainPatch(final String filename)
      throws JSONException, IOException {
    final JSONObject json = new JSONObject(Files.getFileAsString(filename));
    final int size = json.getInt("size");
    final JSONArray tileArray = json.getJSONArray("tiles");
    if (tileArray.length() != size * size) {
      throw new IOException(
        String.format(
          "Terrain patch size mismatch %d != %d: %s", tileArray.length(), size * size, filename));
    }
    final int[] tiles = new int[size];
    for (int i = 0; i < tileArray.length(); ++i) {
      tiles[i] = tileArray.getInt(i);
    }
    return new TerrainPatch(size, tiles);
  }

  public static void saveTerrainPatch(final TerrainPatch patch, final String filename)
      throws JSONException, IOException {
    final JSONObject json = new JSONObject();
    json.put("size", patch.getSize());
    json.put("tiles", Arrays.asList(patch.getTiles()));
    json.write(new FileWriter(filename));
  }
}
