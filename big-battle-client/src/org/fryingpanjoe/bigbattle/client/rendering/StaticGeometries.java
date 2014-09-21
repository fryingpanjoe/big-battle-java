package org.fryingpanjoe.bigbattle.client.rendering;

import org.lwjgl.util.vector.Vector3f;

public class StaticGeometries {

  // cube (tris)
  public static final float[] CUBE_VERT_XYZ = {
    // top
    -0.5f,  0.5f, -0.5f,
    -0.5f,  0.5f,  0.5f,
     0.5f,  0.5f,  0.5f,
     0.5f,  0.5f,  0.5f,
     0.5f,  0.5f, -0.5f,
    -0.5f,  0.5f, -0.5f,

    // bottom
    -0.5f, -0.5f, -0.5f,
     0.5f, -0.5f, -0.5f,
     0.5f, -0.5f,  0.5f,
     0.5f, -0.5f,  0.5f,
    -0.5f, -0.5f,  0.5f,
    -0.5f, -0.5f, -0.5f,

    // front
    -0.5f, -0.5f,  0.5f,
     0.5f, -0.5f,  0.5f,
     0.5f,  0.5f,  0.5f,
     0.5f,  0.5f,  0.5f,
    -0.5f,  0.5f,  0.5f,
    -0.5f, -0.5f,  0.5f,

    // back
    -0.5f, -0.5f, -0.5f,
    -0.5f,  0.5f, -0.5f,
     0.5f,  0.5f, -0.5f,
     0.5f,  0.5f, -0.5f,
     0.5f, -0.5f, -0.5f,
    -0.5f, -0.5f, -0.5f,

    // right
     0.5f, -0.5f, -0.5f,
     0.5f,  0.5f, -0.5f,
     0.5f,  0.5f,  0.5f,
     0.5f,  0.5f,  0.5f,
     0.5f, -0.5f,  0.5f,
     0.5f, -0.5f, -0.5f,

    // left
    -0.5f, -0.5f, -0.5f,
    -0.5f, -0.5f,  0.5f,
    -0.5f,  0.5f,  0.5f,
    -0.5f,  0.5f,  0.5f,
    -0.5f,  0.5f, -0.5f,
    -0.5f, -0.5f, -0.5f,
  };

  public static final float[] CUBE_VERT_UV = {
    // top
    0.f, 1.f,
    0.f, 0.f,
    1.f, 0.f,
    1.f, 0.f,
    1.f, 1.f,
    0.f, 1.f,

    // bottom
    1.f, 1.f,
    0.f, 1.f,
    0.f, 0.f,
    0.f, 0.f,
    1.f, 0.f,
    1.f, 1.f,

    // front
    0.f, 0.f,
    1.f, 0.f,
    1.f, 1.f,
    1.f, 1.f,
    0.f, 1.f,
    0.f, 0.f,

    // back
    1.f, 0.f,
    1.f, 1.f,
    0.f, 1.f,
    0.f, 1.f,
    0.f, 0.f,
    1.f, 0.f,

    // right
    1.f, 0.f,
    1.f, 1.f,
    0.f, 1.f,
    0.f, 1.f,
    0.f, 0.f,
    1.f, 0.f,

    // left
    0.f, 0.f,
    1.f, 0.f,
    1.f, 1.f,
    1.f, 1.f,
    0.f, 1.f,
    0.f, 0.f,
  };

  public static final float[] CUBE_VERT_NORM = {
    // top
     0.f,  1.f,  0.f,
     0.f,  1.f,  0.f,
     0.f,  1.f,  0.f,
     0.f,  1.f,  0.f,
     0.f,  1.f,  0.f,
     0.f,  1.f,  0.f,

    // bottom
     0.f, -1.f,  0.f,
     0.f, -1.f,  0.f,
     0.f, -1.f,  0.f,
     0.f, -1.f,  0.f,
     0.f, -1.f,  0.f,
     0.f, -1.f,  0.f,

    // front
     0.f,  0.f,  1.f,
     0.f,  0.f,  1.f,
     0.f,  0.f,  1.f,
     0.f,  0.f,  1.f,
     0.f,  0.f,  1.f,
     0.f,  0.f,  1.f,

    // back
     0.f,  0.f, -1.f,
     0.f,  0.f, -1.f,
     0.f,  0.f, -1.f,
     0.f,  0.f, -1.f,
     0.f,  0.f, -1.f,
     0.f,  0.f, -1.f,

    // right
     1.f,  0.f,  0.f,
     1.f,  0.f,  0.f,
     1.f,  0.f,  0.f,
     1.f,  0.f,  0.f,
     1.f,  0.f,  0.f,
     1.f,  0.f,  0.f,

    // left
    -1.f,  0.f,  0.f,
    -1.f,  0.f,  0.f,
    -1.f,  0.f,  0.f,
    -1.f,  0.f,  0.f,
    -1.f,  0.f,  0.f,
    -1.f,  0.f,  0.f,
  };

  // top quad (tristrip)
  public static final float[] TOP_QUAD_VERT_TRISTRIP_XYZ = {
    -0.5f,  0.f,  0.5f,
    -0.5f,  0.f, -0.5f,
     0.5f,  0.f,  0.5f,
     0.5f,  0.f, -0.5f,
  };

  public static final float[] TOP_QUAD_VERT_TRISTRIP_UV = {
    0.f, 1.f,
    0.f, 0.f,
    1.f, 1.f,
    0.f, 1.f,
  };

  public static final float[] TOP_QUAD_VERT_TRISTRIP_NORM = {
    0.f,  1.f,  0.f,
    0.f,  1.f,  0.f,
    0.f,  1.f,  0.f,
    0.f,  1.f,  0.f,
  };

  // top quad (tris)
  public static final float[] TOP_QUAD_TRI_VERT_XYZ = {
    -0.5f,  0.f, -0.5f,
    -0.5f,  0.f,  0.5f,
     0.5f,  0.f,  0.5f,
     0.5f,  0.f,  0.5f,
     0.5f,  0.f, -0.5f,
    -0.5f,  0.f, -0.5f,
  };

  public static final float[] TOP_QUAD_TRI_VERT_UV = {
    0.f, 1.f,
    0.f, 0.f,
    1.f, 0.f,
    1.f, 0.f,
    1.f, 1.f,
    0.f, 1.f,
  };

  public static final float[] TOP_QUAD_TRI_VERT_NORM = {
    0.f,  1.f,  0.f,
    0.f,  1.f,  0.f,
    0.f,  1.f,  0.f,
    0.f,  1.f,  0.f,
    0.f,  1.f,  0.f,
    0.f,  1.f,  0.f,
  };

  // pyramid (tris)
  public static final float[] PYRAMID_VERT_XYZ = {
    // front
     0.f,  0.5f,  0.f,
    -0.5f, -0.5f,  0.5f,
     0.5f, -0.5f,  0.5f,

    // back
     0.f,  0.5f,  0.f,
    -0.5f, -0.5f, -0.5f,
     0.5f, -0.5f, -0.5f,

    // right
     0.f,  0.5f,  0.f,
     0.5f, -0.5f,  0.5f,
     0.5f, -0.5f, -0.5f,

    // left
     0.f,  0.5f,  0.f,
    -0.5f, -0.5f,  0.5f,
     0.5f, -0.5f,  0.5f,

    // bottom (two tris)
    -0.5f, -0.5f,  0.5f,
     0.5f, -0.5f,  0.5f,
    -0.5f, -0.5f, -0.5f,

    -0.5f, -0.5f, -0.5f,
     0.5f, -0.5f,  0.5f,
     0.5f, -0.5f, -0.5f,
  };

  public static float[] PYRAMID_VERT_UV = {
     // front
     0.5f, 1.f,
     0.f, 0.f,
     1.f, 0.f,

     // back
     0.5f, 1.f,
     0.f, 0.f,
     1.f, 0.f,

     // right
     0.5f, 1.f,
     0.f, 0.f,
     1.f, 0.f,

     // left
     0.5f, 1.f,
     0.f, 0.f,
     1.f, 0.f,

     // bottom
     0.f, 1.f,
     1.f, 1.f,
     0.f, 0.f,

     0.f, 0.f,
     1.f, 1.f,
     1.f, 0.f,
  };

  public static final float[] PYRAMID_VERT_NORM = calcFaceNorms(PYRAMID_VERT_XYZ);

  /**
   * Calculate face normals and use them as vertex normals
   *
   * @param xyz the vertices
   * @return an array of normal components
   */
  private static final float[] calcFaceNorms(final float[] xyz) {
    assert xyz.length % (3 * 3) == 0 : "vertex component mismatch";
    final float[] norms = new float[xyz.length];
    final Vector3f u = new Vector3f();
    final Vector3f v = new Vector3f();
    final Vector3f w = new Vector3f();
    final Vector3f uv = new Vector3f();
    final Vector3f uw = new Vector3f();
    final Vector3f n = new Vector3f();
    for (int i = 0; i < xyz.length; i += 9) {
      u.x = xyz[i + 0]; u.y = xyz[i + 1]; u.z = xyz[i + 2];
      v.x = xyz[i + 3]; v.y = xyz[i + 4]; v.z = xyz[i + 5];
      w.x = xyz[i + 6]; w.y = xyz[i + 7]; w.z = xyz[i + 8];
      Vector3f.sub(u, v, uv);
      Vector3f.sub(u, w, uw);
      Vector3f.cross(uv, uw, n);
      n.normalise();
      norms[i + 0] = n.x; norms[i + 1] = n.y; norms[i + 2] = n.z;
      norms[i + 3] = n.x; norms[i + 4] = n.y; norms[i + 5] = n.z;
      norms[i + 6] = n.x; norms[i + 7] = n.y; norms[i + 8] = n.z;
    }
    return norms;
  }
}
