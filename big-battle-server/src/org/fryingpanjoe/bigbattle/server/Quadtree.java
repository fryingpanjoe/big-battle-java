package org.fryingpanjoe.bigbattle.server;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class Quadtree<T> {

  private static final int MAX_OBJECT_COUNT = 16;
  private static final int MIN_DEPTH = 2;
  private static final int MAX_DEPTH = 6;
  private static final float ROOT_SIZE = Float.MAX_VALUE;

  public static class Bound {

    public final float x0;
    public final float y0;
    public final float x1;
    public final float y1;

    public Bound(final float x0, final float y0, final float x1, final float y1) {
      this.x0 = x0;
      this.y0 = y0;
      this.x1 = x1;
      this.y1 = y1;
    }

    public boolean intersectPoint(final float x, final float y) {
      return Intersecting.pointAabb(x, y, this.x0, this.y0, this.x1, this.y1);
    }

    public boolean intersectAabb(final float x0, final float y0, final float x1, final float y1) {
      return Intersecting.aabbAabb(this.x0, this.y0, this.x1, this.y1, x0, y0, x1, y1);
    }

    public List<Bound> getChildBounds() {
      final float cx = (this.x1 - this.x0) * 0.5f;
      final float cy = (this.y1 - this.y0) * 0.5f;
      return ImmutableList.of(
        // top left
        new Bound(this.x0, this.y0, cx, cy),
        // top right
        new Bound(cx, this.y0, this.x1, cy),
        // bottom right
        new Bound(cx, cy, this.x1, this.y1),
        // bottom left
        new Bound(this.x0, cy, cx, this.y1));
    }
  }

  private static class Node<T> {

    private final Bound bound;
    private final List<T> objects;
    private final List<Node<T>> children;

    public Node(final Bound bound) {
      this.bound = bound;
      this.objects = new ArrayList<>();
      this.children = new ArrayList<>();
    }

    public boolean insert(final T object, final float x, final float y, final int depth) {
      if (this.bound.intersectPoint(x, y)) {
        if ((depth >= MIN_DEPTH) &&
            (depth >= MAX_DEPTH || this.objects.size() < MAX_OBJECT_COUNT)) {
          this.objects.add(object);
          return true;
        } else {
          if (this.children.isEmpty()) {
            for (final Bound childBound : this.bound.getChildBounds()) {
              this.children.add(new Node<>(childBound));
            }
          }
          for (final Node<T> child : this.children) {
            if (child.insert(object, x, y, depth + 1)) {
              return true;
            }
          }
          return false;
        }
      } else {
        return false;
      }
    }

    public boolean remove(final T object) {
      if (this.objects.remove(object)) {
        return true;
      } else {
        for (final Node<T> child : this.children) {
          if (child.remove(object)) {
            return true;
          }
        }
      }
      return false;
    }

    public void queryPoint(final List<T> result, final float x, final float y) {
      if (this.bound.intersectPoint(x, y)) {
        result.addAll(this.objects);
        for (final Node<T> child : this.children) {
          child.queryPoint(result, x, y);
        }
      }
    }

    public void queryAabb(final List<T> result,
                          final float x0, final float y0, final float x1, final float y1) {
      if (this.bound.intersectAabb(x0, y0, x1, y1)) {
        result.addAll(this.objects);
        for (final Node<T> child : this.children) {
          child.queryAabb(result, x0, y0, x1, y1);
        }
      }
    }

    public void queryAll(final List<T> result) {
      result.addAll(this.objects);
      for (final Node<T> child : this.children) {
        child.queryAll(result);
      }
    }
  }

  private final Node<T> root;

  public Quadtree() {
    this.root = new Node<>(
      new Bound(-ROOT_SIZE * 0.5f, -ROOT_SIZE * 0.5f, ROOT_SIZE, ROOT_SIZE));
  }

  public boolean insert(final T object, final float x, final float y) {
    return this.root.insert(object, x, y, 0);
  }

  public boolean remove(final T object) {
    return this.root.remove(object);
  }

  public List<T> queryPoint(final float x, final float y) {
    final List<T> result = new ArrayList<>();
    this.root.queryPoint(result, x, y);
    return result;
  }

  public List<T> queryAabb(final float x0, final float y0, final float x1, final float y1) {
    final List<T> result = new ArrayList<>();
    this.root.queryAabb(result, x0, y0, x1, y1);
    return result;
  }

  public List<T> queryAll() {
    final List<T> result = new ArrayList<>();
    this.root.queryAll(result);
    return result;
  }
}
