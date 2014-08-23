package org.fryingpanjoe.bigbattle.server;

import org.fryingpanjoe.bigbattle.server.events.EntityKilledEvent;
import org.fryingpanjoe.bigbattle.server.events.EntityMovedEvent;
import org.fryingpanjoe.bigbattle.server.events.EntitySpawnedEvent;
import org.fryingpanjoe.bigbattle.server.game.ServerArea;

import com.google.common.eventbus.Subscribe;

public class ServerAreaManager {

  private final Quadtree<ServerArea> quadtree;

  public ServerAreaManager(final Quadtree<ServerArea> quadtree) {
    this.quadtree = quadtree;
  }

  @Subscribe
  public void onEntitySpawnedEvent(final EntitySpawnedEvent event) {
    final float x = event.entity.getEntity().getPos().x;
    final float y = event.entity.getEntity().getPos().y;
    for (final ServerArea area : this.quadtree.queryPoint(x, y)) {
      if (area.intersectPoint(x, y)) {
        area.entityEntered(event.entity);
        event.entity.setArea(area);
        break;
      }
    }
  }

  @Subscribe
  public void onEntityKilledEvent(final EntityKilledEvent event) {
    final ServerArea area = event.entity.getArea();
    if (area != null) {
      area.entityLeft(event.entity);
    }
  }

  @Subscribe
  public void onEntityMovedEvent(final EntityMovedEvent event) {
    final float x = event.entity.getEntity().getPos().x;
    final float y = event.entity.getEntity().getPos().y;
    for (final ServerArea area : this.quadtree.queryPoint(x, y)) {
      if (area.intersectPoint(x, y) && area != event.entity.getArea()) {
        event.entity.getArea().entityLeft(event.entity);
        area.entityEntered(event.entity);
        event.entity.setArea(area);
        break;
      }
    }
  }

  /*
  public List<ServerArea> intersectPoint(final float x, final float y) {
    final List<ServerArea> intersected = this.quadtree.queryPoint(x, y);
    final List<ServerArea> result = new ArrayList<>(intersected.size());
    for (final ServerArea area : intersected) {
      if (area.intersectPoint(x, y)) {
        result.add(area);
      }
    }
    return result;
  }

  public List<ServerArea> intersectSphere(final float x, final float y, final float radius) {
    final Quadtree.Bound bound = new Quadtree.Bound(
      x - radius, y - radius, radius * 2.f, radius * 2.f);
    final List<ServerArea> intersected = this.areas.queryRect(bound);
    final List<ServerArea> result = new ArrayList<>(intersected.size());
    for (final ServerArea area : intersected) {
      if (area.intersectSphere(x, y, radius)) {
        result.add(area);
      }
    }
    return result;
  }*/
}
