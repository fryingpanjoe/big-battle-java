package org.fryingpanjoe.bigbattle.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.server.game.ServerEntity;
import org.fryingpanjoe.bigbattle.server.game.ServerNotice;

public class ServerNoticeManager {

  private static final Logger LOG = Logger.getLogger(ServerNoticeManager.class.getName());

  private final ServerEntityManager entityManager;
  private final List<ServerNotice> notices;

  public ServerNoticeManager(final ServerEntityManager entityManager) {
    this.entityManager = entityManager;
    this.notices = new ArrayList<>();
  }

  public void addNotice(final ServerNotice notice) {
    this.notices.add(notice);
  }

  public void removeNotice(final ServerNotice notice) {
    this.notices.remove(notice);
  }

  public void updateNotices() {
    for (final ServerNotice notice : this.notices) {
      final List<ServerEntity> noticedEntityList = this.entityManager.getEntitiesInSphere(
        notice.getEntity().getEntity().getX(),
        notice.getEntity().getEntity().getY(),
        notice.getRange());
      final Set<Integer> noticedEntityIds = new HashSet<>();
      for (final ServerEntity entity : noticedEntityList) {
        final int entityId = entity.getEntity().getId();
        if (!notice.isNoticed(entityId)) {
          LOG.info(
            String.format(
              "Entity %d noticed %d", notice.getEntity().getEntity().getId(), entityId));
          notice.entityNoticed(entityId);
        }
        noticedEntityIds.add(entityId);
      }
      for (final int watchedEntityId : notice.getNoticed()) {
        if (!noticedEntityIds.contains(watchedEntityId)) {
          LOG.info(
            String.format(
              "Entity %d lost %d", notice.getEntity().getEntity().getId(), watchedEntityId));
          notice.entityLost(watchedEntityId);
        }
      }
    }
  }
}
