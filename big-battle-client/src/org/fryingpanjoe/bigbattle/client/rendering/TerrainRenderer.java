package org.fryingpanjoe.bigbattle.client.rendering;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.fryingpanjoe.bigbattle.common.Constants;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainPatch;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class TerrainRenderer {

  private static final float SQRT_TWO = (float) Math.sqrt(2.f);
  private static final float SQRT_TWO_OVER_TWO = SQRT_TWO * 0.5f;

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

  private final Texture atlas;
  private final int atlasTilesPerRow;
  private final float atlasTileSizeU;
  private final float atlasTileSizeV;
  private final int shader;
  private final int vbo;
  private final int vboSize;
  private final FloatBuffer vboBuffer;
  private final float[] vboBufferArray;

  public TerrainRenderer() throws IOException {
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

  public void renderTerrainPatch(final IsometricCamera camera, final TerrainPatch patch) {
    final float d = camera.getScale();
    final float cx = camera.getPosition().x;
    final float cy = camera.getPosition().y;
    final float minX = -d - 1.f;
    final float maxX =  d + 1.f;
    final float minY = -2.f * SQRT_TWO * d - 1.f;
    final float maxY =  2.f * SQRT_TWO * d + 1.f;
    final Vector2f tileBL = rot45(minX, maxY);
    final Vector2f tileBR = rot45(maxX, maxY);
    final Vector2f tileTL = rot45(minX, minY);
    final Vector2f tileTR = rot45(maxX, minY);
    final int tileMinX = Math.max(
      0, (int)((cx + tileTL.x) / Constants.TILE_SIZE_IN_UNITS));
    final int tileMaxX = Math.min(
      patch.getSize(), (int)((cx + tileBR.x) / Constants.TILE_SIZE_IN_UNITS));
    final int tileMinY = Math.max(
      0, (int)((cy + tileTR.y) / Constants.TILE_SIZE_IN_UNITS));
    final int tileMaxY = Math.min(
      patch.getSize(), (int)((cy + tileBL.y) / Constants.TILE_SIZE_IN_UNITS));
    final Vector2f tilePos = new Vector2f(0.f, 0.f);
    final float[] vertData = this.vboBufferArray;
    int vertDataIndex = 0;
    for (int y = tileMinY; y < tileMaxY; ++y) {
      for (int x = tileMinX; x < tileMaxX; ++x) {
        tilePos.x = (x * Constants.TILE_SIZE_IN_UNITS) - cx;
        tilePos.y = -((y * Constants.TILE_SIZE_IN_UNITS) - cy);
        rot45(tilePos);
        if (tilePos.x < minX || tilePos.x > maxX || tilePos.y < minY || tilePos.y > maxY) {
          continue;
        }
        final int tile = patch.getTiles()[x + y * patch.getSize()];
        final int vertCount = 6; //StaticGeometry.CUBE_VERT_XYZ.length / 3;
        for (int i = 0; i < vertCount; ++i) {
          vertData[vertDataIndex++] =
            StaticGeometries.CUBE_VERT_XYZ[(i * 3) + 0] + (float) x * Constants.TILE_SIZE_IN_UNITS;
          vertData[vertDataIndex++] = 0.f;
          vertData[vertDataIndex++] =
            StaticGeometries.CUBE_VERT_XYZ[(i * 3) + 2] + (float) y * Constants.TILE_SIZE_IN_UNITS;
          vertData[vertDataIndex++] =
            (StaticGeometries.CUBE_VERT_UV[(i * 2) + 0] + (tile % this.atlasTilesPerRow)) *
            this.atlasTileSizeU;
          vertData[vertDataIndex++] =
            (StaticGeometries.CUBE_VERT_UV[(i * 2) + 1] + (tile / this.atlasTilesPerRow)) *
            this.atlasTileSizeV;
          vertData[vertDataIndex++] = StaticGeometries.CUBE_VERT_NORM[(i * 3) + 0];
          vertData[vertDataIndex++] = StaticGeometries.CUBE_VERT_NORM[(i * 3) + 1];
          vertData[vertDataIndex++] = StaticGeometries.CUBE_VERT_NORM[(i * 3) + 2];
        }
      }
    }
    //System.out.println(String.format("Rendering tiles %d,%d to %d,%d with %d vertices", tileMinX, tileMinY, tileMaxX, tileMaxY, vertDataIndex));
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

    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertDataIndex);

    GL20.glUseProgram(0);

    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
    GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
  }

  private static Vector2f rot45(final float x, final float y) {
    final Vector2f v = new Vector2f(x, y);
    rot45(v);
    return v;
  }

  private static void rot45(final Vector2f v) {
    final float x = v.x;
    final float y = v.y;
    v.x = (x + y) * SQRT_TWO_OVER_TWO;
    v.y = (y - x) * SQRT_TWO_OVER_TWO;
  }
}
