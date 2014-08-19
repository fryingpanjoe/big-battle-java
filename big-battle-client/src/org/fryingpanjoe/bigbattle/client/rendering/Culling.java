package org.fryingpanjoe.bigbattle.client.rendering;


public class Culling {

  private static final float SQRT_TWO = (float) Math.sqrt(2.f);
  private static final float SQRT_TWO_OVER_TWO = SQRT_TWO * 0.5f;

  private Culling() {
  }

  public static boolean cullPoint(final IsometricCamera camera,
                                  final float x,
                                  final float y) {
    final float d = camera.getScale();
    final float minX = -d - 1.f;
    final float maxX =  d + 1.f;
    final float minY = -2.f * SQRT_TWO * d - 1.f;
    final float maxY =  2.f * SQRT_TWO * d + 1.f;
    // project point inside camera frustum
    final float relX = x - camera.getPos().x;
    final float relY = -(y - camera.getPos().y);
    final float projX = (relX + relY) * SQRT_TWO_OVER_TWO;
    final float projY = (relY - relX) * SQRT_TWO_OVER_TWO;
    return projX < minX || projX > maxX || projY < minY || projY > maxY;
  }

  public static boolean cullSphere(final IsometricCamera camera,
                                   final float x,
                                   final float y,
                                   final float radius) {
    // approximate using bounding sphere
    final float camRadius = 2.f * SQRT_TWO * camera.getScale() + 1.f;
    final float combinedRadius = camRadius + radius;
    final float relX = x - camera.getPos().x;
    final float relY = -(y - camera.getPos().y);
    final float projX = (relX + relY) * SQRT_TWO_OVER_TWO;
    final float projY = (relY - relX) * SQRT_TWO_OVER_TWO;
    return (projX * projX + projY * projY) < (combinedRadius * combinedRadius);
  }
}
