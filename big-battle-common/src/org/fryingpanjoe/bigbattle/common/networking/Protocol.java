package org.fryingpanjoe.bigbattle.common.networking;

import java.nio.ByteBuffer;
import java.util.EnumSet;

import org.fryingpanjoe.bigbattle.common.events.EnterGameEvent;
import org.fryingpanjoe.bigbattle.common.events.EntityLostEvent;
import org.fryingpanjoe.bigbattle.common.events.EntityNoticedEvent;
import org.fryingpanjoe.bigbattle.common.game.Entity;
import org.fryingpanjoe.bigbattle.common.game.EntityDefinition;
import org.fryingpanjoe.bigbattle.common.game.EntityDefinitions;
import org.fryingpanjoe.bigbattle.common.game.PlayerInput;
import org.fryingpanjoe.bigbattle.common.game.PlayerInput.Action;

public class Protocol {

  public enum PacketType {
    Hello,
    Goodbye,
    EnterGameEvent,
    EntityDelta,
    Entity,
    PlayerInput,
    EntityNoticedEvent,
    EntityLostEvent,
  }

  private Protocol() {
  }

  public static void writePacketHeader(final ByteBuffer packet, final PacketType type) {
    packet.put((byte) type.ordinal());
  }

  public static PacketType readPacketHeader(final ByteBuffer packet) {
    final byte type = packet.get();
    if (type < 0 || type >= PacketType.values().length) {
      throw new IndexOutOfBoundsException("Bad packet type: " + type);
    }
    return PacketType.values()[type];
  }

  public static void writeEnterGameEvent(final ByteBuffer packet,
                                         final EnterGameEvent event) {
    packet.putInt(event.clientId);
    packet.putInt(event.entityId);
  }

  public static EnterGameEvent readEnterGameEvent(final ByteBuffer packet) {
    final int clientId = packet.getInt();
    final int entityId = packet.getInt();
    return new EnterGameEvent(clientId, entityId);
  }

  public static void writeEntityDelta(final ByteBuffer packet,
                                      final Entity entity) {
    final byte bits = entity.getUpdateBits();
    packet.put(bits);
    if ((bits & Entity.POSITION_BIT) != 0) {
      packet.putFloat(entity.getX());
      packet.putFloat(entity.getY());
    }
    if ((bits & Entity.VELOCITY_BIT) != 0) {
      packet.putFloat(entity.getVelocityX());
      packet.putFloat(entity.getVelocityY());
    }
    if ((bits & Entity.ROTATION_BIT) != 0) {
      packet.putFloat(entity.getRotation());
    }
  }

  public static void readEntityDelta(final ByteBuffer packet,
                                     final Entity entity) {
    final byte bits = packet.get();
    if ((bits & Entity.POSITION_BIT) != 0) {
      final float x = packet.getFloat();
      final float y = packet.getFloat();
      entity.setPosition(x, y);
    }
    if ((bits & Entity.VELOCITY_BIT) != 0) {
      final float x = packet.getFloat();
      final float y = packet.getFloat();
      entity.setVelocity(x, y);
    }
    if ((bits & Entity.ROTATION_BIT) != 0) {
      entity.setRotation(packet.getFloat());
    }
    entity.resetUpdateBits(bits);
  }

  public static void writeEntity(final ByteBuffer packet,
                                 final Entity entity) {
    packet.putInt(entity.getId());
    packet.putInt(entity.getDef().getId());
    packet.putFloat(entity.getX());
    packet.putFloat(entity.getY());
    packet.putFloat(entity.getVelocityX());
    packet.putFloat(entity.getVelocityY());
    packet.putFloat(entity.getRotation());
  }

  public static Entity readEntity(final ByteBuffer packet) {
    final int id = packet.getInt();
    final EntityDefinition def = EntityDefinitions.getEntityDefinition(packet.getInt());
    final float posX = packet.getFloat();
    final float posY = packet.getFloat();
    final float velX = packet.getFloat();
    final float velY = packet.getFloat();
    final float rotation = packet.getFloat();
    return new Entity(id, def, posX, posY, velX, velY, rotation);
  }

  public static void writePlayerInput(final ByteBuffer packet,
                                      final PlayerInput playerInput) {
    packet.putInt(playerInputActionsToInteger(playerInput.getActions()));
    packet.putFloat(playerInput.getRotation());
  }

  public static PlayerInput readPlayerInput(final ByteBuffer packet) {
    final EnumSet<Action> actions = playerInputActionsFromInteger(packet.getInt());
    final float rotation = packet.getFloat();
    return new PlayerInput(actions, rotation);
  }

  public static void writeEntityNoticedEvent(final ByteBuffer packet,
                                             final EntityNoticedEvent event) {
    writeEntity(packet, event.entity);
  }

  public static EntityNoticedEvent readEntityNoticedEvent(final ByteBuffer packet) {
    return new EntityNoticedEvent(readEntity(packet));
  }

  public static void writeEntityLostEvent(final ByteBuffer packet,
                                          final EntityLostEvent event) {
    packet.putInt(event.entityId);
  }

  public static EntityLostEvent readEntityLostEvent(final ByteBuffer packet) {
    final int entityId = packet.getInt();
    return new EntityLostEvent(entityId);
  }

  private static int playerInputActionsToInteger(final EnumSet<Action> actions) {
    int flags = 0;
    for (final Action action : actions) {
      flags |= (1 << action.ordinal());
    }
    return flags;
  }

  private static EnumSet<Action> playerInputActionsFromInteger(final int flags) {
    final EnumSet<Action> actions = EnumSet.noneOf(Action.class);
    for (final Action action : Action.values()) {
      if ((flags & (1 << action.ordinal())) != 0) {
        actions.add(action);
      }
    }
    return actions;
  }
}
