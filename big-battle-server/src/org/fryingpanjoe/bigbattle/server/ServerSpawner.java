package org.fryingpanjoe.bigbattle.server;

import org.fryingpanjoe.bigbattle.common.game.Entity;
import org.fryingpanjoe.bigbattle.common.game.EntityDefinition;
import org.fryingpanjoe.bigbattle.common.game.EntityDefinitions;
import org.fryingpanjoe.bigbattle.common.game.Player;
import org.fryingpanjoe.bigbattle.server.events.EntitySpawnedEvent;
import org.fryingpanjoe.bigbattle.server.events.PlayerSpawnedEvent;
import org.fryingpanjoe.bigbattle.server.game.ServerEntity;
import org.fryingpanjoe.bigbattle.server.game.ServerPlayer;

import com.google.common.eventbus.EventBus;

public class ServerSpawner {

  private final EventBus eventBus;
  private int entityIdGenerator;
  private int playerIdGenerator;

  public ServerSpawner(final EventBus eventBus) {
    this.eventBus = eventBus;
    this.entityIdGenerator = 1;
    this.playerIdGenerator = 1;
  }

  public ServerEntity spawnEntity(final EntityDefinition def, final float x, final float y) {
    final ServerEntity entity = new ServerEntity(
      new Entity(getNextEntityId(), def, x, y, 0.f, 0.f, 0.f));
    this.eventBus.post(new EntitySpawnedEvent(entity));
    return entity;
  }

  public ServerPlayer spawnPlayer(final int clientId,
                                  final String name,
                                  final float x,
                                  final float y) {
    final ServerEntity entity = spawnEntity(EntityDefinitions.PLAYER, x, y);
    final ServerPlayer player = new ServerPlayer(
      new Player(getNextPlayerId(), clientId, entity.getEntity().getId(), name), entity);
    this.eventBus.post(new PlayerSpawnedEvent(player));
    return player;
  }

  private int getNextEntityId() {
    return this.entityIdGenerator++;
  }

  private int getNextPlayerId() {
    return this.playerIdGenerator++;
  }
}
