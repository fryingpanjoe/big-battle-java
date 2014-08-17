package org.fryingpanjoe.bigbattle.common.networking;

import java.nio.ByteBuffer;
import java.util.EnumSet;

import org.fryingpanjoe.bigbattle.common.game.Entity;
import org.fryingpanjoe.bigbattle.common.game.EntityDefinition;
import org.fryingpanjoe.bigbattle.common.game.EntityDefinitions;
import org.fryingpanjoe.bigbattle.common.game.PlayerInput;
import org.fryingpanjoe.bigbattle.common.game.PlayerInput.Action;

public class Protocol {

  public enum PacketType {
    EntityDelta,
    Entity,
    PlayerInputDelta,
    PlayerInput,
  }

  private static final byte ENTITY_POS_BIT      = 0b00000001;
  private static final byte ENTITY_VEL_BIT      = 0b00000010;
  private static final byte ENTITY_ACC_BIT      = 0b00000100;
  private static final byte ENTITY_FORCE_BIT    = 0b00001000;
  private static final byte ENTITY_ROTATION_BIT = 0b00010000;

  private static final byte PLAYER_INPUT_ACTION_BIT     = 0b01;
  private static final byte PLAYER_INPUT_ROTATION_BIT   = 0b10;

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

  public static void writeEntityDelta(final ByteBuffer packet,
                                      final Entity oldEntity,
                                      final Entity newEntity) {
    int bits = 0;
    if (!newEntity.getPos().equals(oldEntity.getPos())) {
      bits |= ENTITY_POS_BIT;
    }
    if (!newEntity.getVel().equals(oldEntity.getVel())) {
      bits |= ENTITY_VEL_BIT;
    }
    if (!newEntity.getAcc().equals(oldEntity.getAcc())) {
      bits |= ENTITY_ACC_BIT;
    }
    if (!newEntity.getForce().equals(oldEntity.getForce())) {
      bits |= ENTITY_FORCE_BIT;
    }
    if (Math.abs(newEntity.getRotation() - oldEntity.getRotation()) > 0.001f) {
      bits |= ENTITY_ROTATION_BIT;
    }
    packet.putInt(bits);
    if ((bits & ENTITY_POS_BIT) != 0) {
      packet.putFloat(newEntity.getPos().x);
      packet.putFloat(newEntity.getPos().y);
    }
    if ((bits & ENTITY_VEL_BIT) != 0) {
      packet.putFloat(newEntity.getVel().x);
      packet.putFloat(newEntity.getVel().y);
    }
    if ((bits & ENTITY_ACC_BIT) != 0) {
      packet.putFloat(newEntity.getAcc().x);
      packet.putFloat(newEntity.getAcc().y);
    }
    if ((bits & ENTITY_FORCE_BIT) != 0) {
      packet.putFloat(newEntity.getForce().x);
      packet.putFloat(newEntity.getForce().y);
    }
    if ((bits & ENTITY_ROTATION_BIT) != 0) {
      packet.putFloat(newEntity.getRotation());
    }
  }

  public static void readEntityDelta(final ByteBuffer packet,
                                     final Entity entity) {
    final int bits = packet.getInt();
    if ((bits & ENTITY_POS_BIT) != 0) {
      final float x = packet.getFloat();
      final float y = packet.getFloat();
      entity.setPos(x, y);
    }
    if ((bits & ENTITY_VEL_BIT) != 0) {
      final float x = packet.getFloat();
      final float y = packet.getFloat();
      entity.setVel(x, y);
    }
    if ((bits & ENTITY_ACC_BIT) != 0) {
      final float x = packet.getFloat();
      final float y = packet.getFloat();
      entity.setAcc(x, y);
    }
    if ((bits & ENTITY_FORCE_BIT) != 0) {
      final float x = packet.getFloat();
      final float y = packet.getFloat();
      entity.setForce(x, y);
    }
    if ((bits & ENTITY_ROTATION_BIT) != 0) {
      entity.setRotation(packet.getFloat());
    }
  }

  public static void writeEntity(final ByteBuffer packet,
                                 final Entity entity) {
    packet.putInt(entity.getId());
    packet.putInt(entity.getDef().getId());
    packet.putFloat(entity.getPos().x);
    packet.putFloat(entity.getPos().y);
    packet.putFloat(entity.getVel().x);
    packet.putFloat(entity.getVel().y);
    packet.putFloat(entity.getAcc().x);
    packet.putFloat(entity.getAcc().y);
    packet.putFloat(entity.getForce().x);
    packet.putFloat(entity.getForce().y);
    packet.putFloat(entity.getRotation());
  }

  public static Entity readEntity(final ByteBuffer packet) {
    final int id = packet.getInt();
    final EntityDefinition def = EntityDefinitions.getEntityDefinition(packet.getInt());
    final float posX = packet.getFloat();
    final float posY = packet.getFloat();
    final float velX = packet.getFloat();
    final float velY = packet.getFloat();
    final float accX = packet.getFloat();
    final float accY = packet.getFloat();
    final float forceX = packet.getFloat();
    final float forceY = packet.getFloat();
    final float rotation = packet.getFloat();
    return new Entity(id, def, posX, posY, velX, velY, accX, accY, forceX, forceY, rotation);
  }

  public static void writePlayerInputDelta(final ByteBuffer packet,
                                           final PlayerInput oldPlayerInput,
                                           final PlayerInput newPlayerInput) {
    byte bits = 0;
    if (!newPlayerInput.getActions().equals(oldPlayerInput.getActions())) {
      bits |= PLAYER_INPUT_ACTION_BIT;
    }
    if (Math.abs(newPlayerInput.getRotation() - oldPlayerInput.getRotation()) > 0.001f) {
      bits |= PLAYER_INPUT_ROTATION_BIT;
    }
    packet.put(bits);
    if ((bits & PLAYER_INPUT_ACTION_BIT) != 0) {
      packet.putInt(playerInputActionsToInteger(newPlayerInput.getActions()));
    }
    if ((bits & PLAYER_INPUT_ROTATION_BIT) != 0) {
      packet.putFloat(newPlayerInput.getRotation());
    }
  }

  public static void readPlayerInputDelta(final ByteBuffer packet,
                                          final PlayerInput playerInput) {
    final byte bits = packet.get();
    if ((bits & PLAYER_INPUT_ACTION_BIT) != 0) {
      playerInput.setActions(playerInputActionsFromInteger(packet.getInt()));
    }
    if ((bits & PLAYER_INPUT_ROTATION_BIT) != 0) {
      playerInput.setRotation(packet.getFloat());
    }
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

  private static int playerInputActionsToInteger(final EnumSet<Action> actions) {
    int flags = 0;
    for (final Action action : actions) {
      flags |= 1 << action.ordinal();
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
