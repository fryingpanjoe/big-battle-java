package org.fryingpanjoe.bigbattle.common.game;

import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.ImmutableMap;

public class EntityDefinitions {

  public enum EntityDefinitionId {
    Player,
  }

  public static final EntityDefinition PLAYER = new EntityDefinition(
    EntityDefinitionId.Player.ordinal(),
    1.0f, // mass
    0.2f, // radius
    new Vector3f(0.2f, 0.5f, 0.2f), // size
    1.5f, // speed
    1.f); // rotation speed

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
