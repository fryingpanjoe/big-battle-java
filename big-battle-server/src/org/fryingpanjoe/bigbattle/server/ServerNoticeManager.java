package org.fryingpanjoe.bigbattle.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fryingpanjoe.bigbattle.server.game.ServerEntity;
import org.fryingpanjoe.bigbattle.server.game.ServerNotice;

public class ServerNoticeManager {

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
          notice.entityNoticed(entityId);
        }
        noticedEntityIds.add(entityId);
      }
      for (final int watchedEntityId : notice.getNoticed()) {
        if (!noticedEntityIds.contains(watchedEntityId)) {
          notice.entityLost(watchedEntityId);
        }
      }
    }
  }
}
