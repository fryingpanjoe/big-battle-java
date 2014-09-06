package org.fryingpanjoe.bigbattle.server;

import org.fryingpanjoe.bigbattle.common.game.Entity;
import org.fryingpanjoe.bigbattle.common.game.EntityDefinitions;
import org.fryingpanjoe.bigbattle.common.game.Player;
import org.fryingpanjoe.bigbattle.server.game.ServerEntity;
import org.fryingpanjoe.bigbattle.server.game.ServerNotice;
import org.fryingpanjoe.bigbattle.server.game.ServerPlayer;

public class ServerSpawner {

  private static final float PLAYER_NOTICE_RADIUS = 128.f;

  private final ServerEntityManager entityManager;
  private final ServerPlayerManager playerManager;
  private final ServerNoticeManager noticeManager;
  private int entityIdGenerator;

  public ServerSpawner(final ServerEntityManager entityManager,
                       final ServerPlayerManager playerManager,
                       final ServerNoticeManager noticeManager) {
    this.entityManager = entityManager;
    this.playerManager = playerManager;
    this.noticeManager = noticeManager;
    this.entityIdGenerator = 1;
  }

  public ServerPlayer spawnClientPlayer(final int clientId, final float x, final float y) {
    final Entity entity = new Entity(
      getNextEntityId(), EntityDefinitions.PLAYER, x, y, 0.f, 0.f, 0.f, Entity.State.Idle,
      EntityDefinitions.PLAYER.getMaxHealth());
    final ServerEntity serverEntity = new ServerEntity(entity);
    final Player player = new Player(clientId, serverEntity.getEntity().getId());
    final ServerNotice notice = new ServerNotice(serverEntity, PLAYER_NOTICE_RADIUS);
    final ServerPlayer serverPlayer = new ServerPlayer(player, serverEntity, notice);
    this.entityManager.addEntity(serverEntity);
    this.noticeManager.addNotice(notice);
    this.playerManager.addPlayer(serverPlayer);
    return serverPlayer;
  }

  public void killClientPlayer(final int clientId) {
    final ServerPlayer player = this.playerManager.getPlayerByClientId(clientId);
    if (player != null) {
      this.playerManager.removePlayerByClientId(player.getPlayer().getClientId());
      this.noticeManager.removeNotice(player.getNotice());
      this.entityManager.removeEntityById(player.getServerEntity().getEntity().getId());
    }
  }

  private int getNextEntityId() {
    return this.entityIdGenerator++;
  }
}
