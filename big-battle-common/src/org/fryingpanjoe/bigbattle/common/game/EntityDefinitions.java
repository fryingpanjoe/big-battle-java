package org.fryingpanjoe.bigbattle.common.game;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class EntityDefinitions {

  public enum EntityDefinitionId {
    Player,
  }

  public static final EntityDefinition PLAYER = new EntityDefinition(
    EntityDefinitionId.Player.ordinal(),
    1.0f,
    0.5f);

  private static final Map<EntityDefinitionId, EntityDefinition> ALL = ImmutableMap.of(
    EntityDefinitionId.Player, PLAYER);

  private EntityDefinitions() {
  }

  public static EntityDefinition getEntityDefinition(final EntityDefinitionId id) {
    return ALL.get(id);
  }

  public static EntityDefinition getEntityDefinition(final int id) {
    return ALL.get(EntityDefinitionId.values()[id]);
  }
}
