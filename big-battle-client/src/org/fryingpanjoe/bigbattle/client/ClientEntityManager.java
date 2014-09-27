package org.fryingpanjoe.bigbattle.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.client.game.ClientEntity;
import org.fryingpanjoe.bigbattle.client.rendering.RenderEntity;
import org.fryingpanjoe.bigbattle.common.events.EntityLostEvent;
import org.fryingpanjoe.bigbattle.common.events.EntityNoticedEvent;

import com.google.common.eventbus.Subscribe;

public class ClientEntityManager {

  private static final Logger LOG = Logger.getLogger(ClientEntityManager.class.getName());

  private final Map<Integer, ClientEntity> entities;
  private final Map<Integer, RenderEntity> renderEntities;

  public ClientEntityManager() {
    this.entities = new HashMap<>();
    this.renderEntities = new HashMap<>();
  }

  public void updatePositions(final float dt) {
    for (final ClientEntity entity : this.entities.values()) {
      entity.getEntity().setPosition(
        entity.getEntity().getX() + entity.getEntity().getVelocityX() * dt,
        entity.getEntity().getY() + entity.getEntity().getVelocityY() * dt);
    }
  }

  public Map<Integer, ClientEntity> getEntities() {
    return this.entities;
  }

  public Map<Integer, RenderEntity> getRenderEntities() {
    return this.renderEntities;
  }

  @Subscribe
  public void onNoticeEntityEvent(final EntityNoticedEvent event) {
    final ClientEntity entity = this.entities.get(event.entity.getId());
    if (entity != null) {
      entity.getEntity().setPosition(event.entity.getX(), event.entity.getY());
      entity.getEntity().setVelocity(event.entity.getVelocityX(), event.entity.getVelocityY());
      entity.getEntity().setRotation(event.entity.getRotation());
    } else {
      LOG.info(String.format("Noticed entity %d", event.entity.getId()));
      this.entities.put(event.entity.getId(), new ClientEntity(event.entity));
      this.renderEntities.put(event.entity.getId(), new RenderEntity(event.entity));
    }
  }

  @Subscribe
  public void onLostEntityEvent(final EntityLostEvent event) {
    LOG.info(String.format("Lost entity %d", event.entityId));
    this.renderEntities.remove(event.entityId);
    this.entities.remove(event.entityId);
  }
}
