package org.fryingpanjoe.bigbattle.client.rendering;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.fryingpanjoe.bigbattle.common.game.Entity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

public class EntityRenderer {

  private static final Vector3f UP = new Vector3f(0.f, 1.f, 0.f);

  private static final int FLOATS_PER_VERTEX = 3 + 3 + 2;
  private static final int BYTES_PER_FLOAT = 4;

  private static final int VERTEX_STRIDE = FLOATS_PER_VERTEX * BYTES_PER_FLOAT;

  // assume interleaved (xyz, uv, norm)
  private static final int VERTEX_OFFSET_XYZ = 0;
  private static final int VERTEX_OFFSET_UV = 3 * BYTES_PER_FLOAT;
  private static final int VERTEX_OFFSET_NORM = (3 + 2) * BYTES_PER_FLOAT;

  private final FloatBuffer matrixBuffer;
  private final int cubeVertCount;
  private final int cubeVbo;
  private final int shader;
  private final Texture texture;

  public EntityRenderer() throws IOException {
    this.matrixBuffer = FloatBuffer.allocate(4 * 4);
    this.cubeVertCount = StaticGeometries.CUBE_VERT_XYZ.length / 3;
    this.cubeVbo = createVbo(
      StaticGeometries.CUBE_VERT_XYZ,
      StaticGeometries.CUBE_VERT_NORM,
      StaticGeometries.CUBE_VERT_UV);
    this.shader = Shaders.getProgram("entity.vp", "entity.fp");
    this.texture = Textures.getTexture("smiley.png");
  }

  public void renderEntities(final IsometricCamera camera,
                             final Iterable<RenderEntity> renderEntities) {
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture.getTextureID());

    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.cubeVbo);

    GL11.glVertexPointer(3, GL11.GL_FLOAT, VERTEX_STRIDE, VERTEX_OFFSET_XYZ);
    GL11.glTexCoordPointer(2, GL11.GL_FLOAT, VERTEX_STRIDE, VERTEX_OFFSET_UV);
    GL11.glNormalPointer(GL11.GL_FLOAT, VERTEX_STRIDE, VERTEX_OFFSET_NORM);

    GL20.glUseProgram(this.shader);

    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glPushMatrix();
    final Matrix4f matrix = new Matrix4f();
    final Vector3f translate = new Vector3f();
    matrix.setIdentity();
    for (final RenderEntity renderEntity : renderEntities) {
      final Entity entity = renderEntity.getEntity();
      final float x = entity.getX();
      final float y = entity.getY();
      final boolean culled = Culling.cullSphere(
        camera, x, y, entity.getDef().getRadius());
      if (!culled) {
        translate.x = x;
        translate.y = -renderEntity.getSize().y * 0.5f;
        translate.z = y;
        Matrix4f.scale(renderEntity.getSize(), matrix, matrix);
        Matrix4f.rotate(entity.getRotation(), UP, matrix, matrix);
        Matrix4f.translate(translate, matrix, matrix);
        this.matrixBuffer.rewind();
        matrix.store(this.matrixBuffer);
        this.matrixBuffer.rewind();
        GL11.glLoadMatrix(this.matrixBuffer);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.cubeVertCount);
      }
    }
    GL11.glPopMatrix();

    GL20.glUseProgram(0);

    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
    GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
  }

  private static int createVbo(final float[] xyz, final float[] norm, final float[] uv) {
    assert xyz.length == norm.length && xyz.length / 3 == uv.length / 2 : "vertex count mismatch";
    final int vertCount = xyz.length / 3;
    final int floatCount = vertCount * FLOATS_PER_VERTEX;
    final FloatBuffer data = FloatBuffer.allocate(floatCount);
    for (int i = 0; i < vertCount; ++i) {
      // xyz
      data.put(xyz[(i * 3) + 0]);
      data.put(xyz[(i * 3) + 1]);
      data.put(xyz[(i * 3) + 2]);
      // uv
      data.put(uv[(i * 2) + 0]);
      data.put(uv[(i * 2) + 1]);
      // norm
      data.put(norm[(i * 3) + 0]);
      data.put(norm[(i * 3) + 1]);
      data.put(norm[(i * 3) + 2]);
    }
    data.flip();
    final int id = GL15.glGenBuffers();
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
    return id;
  }
}
