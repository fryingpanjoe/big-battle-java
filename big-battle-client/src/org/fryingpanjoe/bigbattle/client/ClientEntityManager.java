package org.fryingpanjoe.bigbattle.client;

import java.util.HashMap;
import java.util.Map;

import org.fryingpanjoe.bigbattle.client.game.ClientEntity;
import org.fryingpanjoe.bigbattle.client.rendering.RenderEntity;
import org.fryingpanjoe.bigbattle.common.events.EntityLostEvent;
import org.fryingpanjoe.bigbattle.common.events.EntityNoticedEvent;

import com.google.common.eventbus.Subscribe;

public class ClientEntityManager {

  private final Map<Integer, ClientEntity> entities;
  private final Map<Integer, RenderEntity> renderEntities;

  public ClientEntityManager() {
    this.entities = new HashMap<>();
    this.renderEntities = new HashMap<>();
  }

  public Map<Integer, ClientEntity> getEntities() {
    return this.entities;
  }

  public Map<Integer, RenderEntity> getRenderEntities() {
    return this.renderEntities;
  }

  @Subscribe
  public void onNoticeEntityEvent(final EntityNoticedEvent event) {
    this.entities.put(event.entity.getId(), new ClientEntity(event.entity));
    this.renderEntities.put(event.entity.getId(), new RenderEntity(event.entity));
  }

  @Subscribe
  public void onLostEntityEvent(final EntityLostEvent event) {
    this.renderEntities.remove(event.entityId);
    this.entities.remove(event.entityId);
  }
}
