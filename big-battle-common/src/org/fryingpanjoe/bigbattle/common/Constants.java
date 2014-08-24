package org.fryingpanjoe.bigbattle.common;

public class Constants {

  public static final float TILE_SIZE_IN_UNITS = 1.f;

  public static final int AREA_SIZE_IN_TILES = 64;
  public static final float AREA_SIZE_IN_UNITS = AREA_SIZE_IN_TILES * TILE_SIZE_IN_UNITS;

  public static final int WORLD_SIZE_IN_AREAS = 128;
  public static final float WORLD_SIZE_IN_UNITS = WORLD_SIZE_IN_AREAS * AREA_SIZE_IN_UNITS;

  public static final float WORLD_MIN_X = 0.f;
  public static final float WORLD_MAX_X = WORLD_SIZE_IN_UNITS;
  public static final float WORLD_MIN_Y = 0.f;
  public static final float WORLD_MAX_Y = WORLD_SIZE_IN_UNITS;
}
