package org.fryingpanjoe.bigbattle.server;

public class Intersecting {

  private Intersecting() {
  }

  public static boolean pointAabb(final float px,
                                  final float py,
                                  final float x0,
                                  final float y0,
                                  final float x1,
                                  final float y1) {
    return px >= x0 && px <= x1 && py >= y0 && py <= y1;
  }

  public static boolean pointSphere(final float px,
                                    final float py,
                                    final float sx,
                                    final float sy,
                                    final float r) {
    final float dx = px - sx;
    final float dy = py - sy;
    return (dx * dx) + (dy * dy) <= (r * r);
  }

  public static boolean sphereSphere(final float x0,
                                     final float y0,
                                     final float r0,
                                     final float x1,
                                     final float y1,
                                     final float r1) {
    return pointSphere(x0, y0, x1, y1, r0 + r1);
  }

  public static boolean sphereAabb(final float sx,
                                   final float sy,
                                   final float r,
                                   final float x0,
                                   final float y0,
                                   final float x1,
                                   final float y1) {
    return pointSphere(Math.min(Math.max(sx, x0), x1), Math.min(Math.max(sy, y0), y1), sx, sy, r);
  }

  public static boolean aabbAabb(final float ax0,
                                 final float ay0,
                                 final float ax1,
                                 final float ay1,
                                 final float bx0,
                                 final float by0,
                                 final float bx1,
                                 final float by1) {
    return !(ax1 < bx0 || ay1 < by0 || ax0 > bx1 || ay0 > by1);
  }
}
