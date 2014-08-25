package org.fryingpanjoe.bigbattle.client.rendering;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Set;

import org.fryingpanjoe.bigbattle.client.ClientTerrainManager;
import org.fryingpanjoe.bigbattle.common.Constants;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainPatch;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainPatchLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.newdawn.slick.opengl.Texture;

import com.google.common.collect.ImmutableSet;

public class TerrainRenderer {

  private static final int ATLAS_TILE_SIZE = 8;

  private static final int TILES_PER_FRAME = 1024; // approxmiate
  private static final int VERTS_PER_TILE = StaticGeometries.CUBE_VERT_XYZ.length / 3;
  private static final int FLOATS_PER_VERT = 3 + 3 + 2; // xyz, norm, uv
  private static final int BYTES_PER_FLOAT = 4;

  private static final int VERTEX_STRIDE_BYTES = FLOATS_PER_VERT * BYTES_PER_FLOAT;

  // assume interleaved (xyz, uv, norm)
  private static final int VERTEX_OFFSET_XYZ = 0;
  private static final int VERTEX_OFFSET_UV = 3 * BYTES_PER_FLOAT;
  private static final int VERTEX_OFFSET_NORM = (3 + 2) * BYTES_PER_FLOAT;

  private final ClientTerrainManager terrainManager;
  private final Texture atlas;
  private final int atlasTilesPerRow;
  private final float atlasTileSizeU;
  private final float atlasTileSizeV;
  private final int shader;
  private final int vbo;
  private final int vboSize;
  private final FloatBuffer vboBuffer;
  private final float[] vboBufferArray;

  public TerrainRenderer(final ClientTerrainManager terrainManager) throws IOException {
    this.terrainManager = terrainManager;
    this.atlas = Textures.getTexture("terrain_atlas.png");
    this.atlasTilesPerRow = this.atlas.getImageWidth() / ATLAS_TILE_SIZE;
    this.atlasTileSizeU = (float)ATLAS_TILE_SIZE / (float)this.atlas.getImageWidth();
    this.atlasTileSizeV = (float)ATLAS_TILE_SIZE / (float)this.atlas.getImageHeight();
    this.shader = Shaders.getProgram("terrain.vp", "terrain.fp");
    final int numFloats = TILES_PER_FRAME * VERTS_PER_TILE * FLOATS_PER_VERT;
    this.vboSize = numFloats * BYTES_PER_FLOAT;
    this.vboBuffer = BufferUtils.createFloatBuffer(numFloats);
    this.vboBufferArray = new float[numFloats];
    this.vbo = GL15.glGenBuffers();
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.vboSize, GL15.GL_STREAM_DRAW);
  }

  public void renderTerrain(final IsometricCamera camera) {
    final float d = camera.getScale();
    final float cx = camera.getPosition().x;
    final float cy = camera.getPosition().y;
    // camera frustum projected on the ground
    final float viewMinX = cx - d * 2f;
    final float viewMaxX = cx + d * 2f;
    final float viewMinY = cy - d * 2f;
    final float viewMaxY = cy + d * 2f;
    // find visible patches
    final Set<TerrainPatchLocation> locations = ImmutableSet.of(
      ClientTerrainManager.getTerrainPatchLocation(viewMinX, viewMinY),
      ClientTerrainManager.getTerrainPatchLocation(viewMaxX, viewMinY),
      ClientTerrainManager.getTerrainPatchLocation(viewMaxX, viewMaxY),
      ClientTerrainManager.getTerrainPatchLocation(viewMinX, viewMaxY));
    // vertex data
    final float[] vertData = this.vboBufferArray;
    int vertDataIndex = 0;
    int vertCount = 0;
    for (final TerrainPatchLocation location : locations) {
      final TerrainPatch patch = this.terrainManager.getTerrainPatch(location);
      final float patchMinX = location.x * Constants.AREA_SIZE_IN_UNITS;
      final float patchMinY = location.y * Constants.AREA_SIZE_IN_UNITS;
      for (int y = 0; y < patch.getSize(); ++y) {
        for (int x = 0; x < patch.getSize(); ++x) {
          final float tileX = patchMinX + x * Constants.TILE_SIZE_IN_UNITS;
          final float tileY = patchMinY + y * Constants.TILE_SIZE_IN_UNITS;
          if (tileX < viewMinX || tileX > viewMaxX || tileY < viewMinY || tileY > viewMaxY) {
            continue;
          }
          final int tile = patch.getTiles()[x + y * patch.getSize()];
          final int tileVertCount = 6; //StaticGeometries.CUBE_VERT_XYZ.length / 3;
          for (int i = 0; i < tileVertCount; ++i) {
            vertData[vertDataIndex++] = 0.5f + StaticGeometries.CUBE_VERT_XYZ[(i * 3) + 0] + tileX;
            vertData[vertDataIndex++] = 0.f; //0.5f - StaticGeometries.CUBE_VERT_XYZ[(i * 3) + 1];
            vertData[vertDataIndex++] = 0.5f + StaticGeometries.CUBE_VERT_XYZ[(i * 3) + 2] + tileY;
            vertData[vertDataIndex++] =
              (StaticGeometries.CUBE_VERT_UV[(i * 2) + 0] + (tile % this.atlasTilesPerRow)) *
              this.atlasTileSizeU;
            vertData[vertDataIndex++] =
              (StaticGeometries.CUBE_VERT_UV[(i * 2) + 1] + (tile / this.atlasTilesPerRow)) *
              this.atlasTileSizeV;
            vertData[vertDataIndex++] = StaticGeometries.CUBE_VERT_NORM[(i * 3) + 0];
            vertData[vertDataIndex++] = StaticGeometries.CUBE_VERT_NORM[(i * 3) + 1];
            vertData[vertDataIndex++] = StaticGeometries.CUBE_VERT_NORM[(i * 3) + 2];
            ++vertCount;
          }
        }
      }
    }
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.atlas.getTextureID());

    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
    this.vboBuffer.clear();
    this.vboBuffer.put(vertData, 0, vertDataIndex);
    this.vboBuffer.flip();
    GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, this.vboBuffer);

    GL11.glVertexPointer(3, GL11.GL_FLOAT, VERTEX_STRIDE_BYTES, VERTEX_OFFSET_XYZ);
    GL11.glTexCoordPointer(2, GL11.GL_FLOAT, VERTEX_STRIDE_BYTES, VERTEX_OFFSET_UV);
    GL11.glNormalPointer(GL11.GL_FLOAT, VERTEX_STRIDE_BYTES, VERTEX_OFFSET_NORM);

    //GL20.glEnableVertexAttribArray(0);
    //GL20.glEnableVertexAttribArray(1);
    //GL20.glEnableVertexAttribArray(2);
    //GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, vertexStride, 0); // xyz
    //GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, vertexStride, 3 * 4); // uv
    //GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, vertexStride, (3 + 2) * 4); // uv

    GL20.glUseProgram(this.shader);
    //GL20.glUniform1i(GL20.glGetUniformLocation(this.shader, "tex"), 0);

    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertCount);

    GL20.glUseProgram(0);

    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
    GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
  }
}
