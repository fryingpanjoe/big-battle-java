package org.fryingpanjoe.bigbattle.common.networking;

import java.nio.ByteBuffer;
import java.util.EnumSet;

import org.fryingpanjoe.bigbattle.common.events.EnterGameEvent;
import org.fryingpanjoe.bigbattle.common.events.EntityLostEvent;
import org.fryingpanjoe.bigbattle.common.events.EntityNoticedEvent;
import org.fryingpanjoe.bigbattle.common.game.Entity;
import org.fryingpanjoe.bigbattle.common.game.Entity.State;
import org.fryingpanjoe.bigbattle.common.game.Entity.UpdateFlag;
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
    packet.put(entityUpdateFlagsToBits(entity.getUpdateFlags()));
    if (entity.getUpdateFlags().contains(Entity.UpdateFlag.Position)) {
      packet.putFloat(entity.getX());
      packet.putFloat(entity.getY());
    }
    if (entity.getUpdateFlags().contains(Entity.UpdateFlag.Velocity)) {
      packet.putFloat(entity.getVelocityX());
      packet.putFloat(entity.getVelocityY());
    }
    if (entity.getUpdateFlags().contains(Entity.UpdateFlag.Rotation)) {
      packet.putFloat(entity.getRotation());
    }
    if (entity.getUpdateFlags().contains(Entity.UpdateFlag.State)) {
      packet.put((byte) entity.getState().ordinal());
    }
  }

  public static void readEntityDelta(final ByteBuffer packet,
                                     final Entity entity) {
    final EnumSet<Entity.UpdateFlag> flags = entityUpdateFlagsFromBits(packet.get());
    if (flags.contains(Entity.UpdateFlag.Position)) {
      final float x = packet.getFloat();
      final float y = packet.getFloat();
      entity.setPosition(x, y);
    }
    if (flags.contains(Entity.UpdateFlag.Velocity)) {
      final float x = packet.getFloat();
      final float y = packet.getFloat();
      entity.setVelocity(x, y);
    }
    if (flags.contains(Entity.UpdateFlag.Rotation)) {
      entity.setRotation(packet.getFloat());
    }
    if (flags.contains(Entity.UpdateFlag.State)) {
      // TODO add error handling
      entity.setState(Entity.State.values()[packet.get()]);
    }
    entity.getUpdateFlags().clear();
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
    packet.put((byte) entity.getState().ordinal());
  }

  public static Entity readEntity(final ByteBuffer packet) {
    final int id = packet.getInt();
    final EntityDefinition def = EntityDefinitions.getEntityDefinition(packet.getInt());
    final float posX = packet.getFloat();
    final float posY = packet.getFloat();
    final float velX = packet.getFloat();
    final float velY = packet.getFloat();
    final float rotation = packet.getFloat();
    final State state = Entity.State.values()[packet.get()];
    return new Entity(id, def, posX, posY, velX, velY, rotation, state);
  }

  public static void writePlayerInput(final ByteBuffer packet,
                                      final PlayerInput playerInput) {
    packet.put(playerInputActionsToBits(playerInput.getActions()));
    packet.putFloat(playerInput.getRotation());
  }

  public static PlayerInput readPlayerInput(final ByteBuffer packet) {
    final EnumSet<Action> actions = playerInputActionsFromBits(packet.get());
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

  private static byte playerInputActionsToBits(final EnumSet<Action> actions) {
    byte bits = 0;
    for (final Action action : actions) {
      assert action.ordinal() < 8;
      bits |= (1 << action.ordinal());
    }
    return bits;
  }

  private static EnumSet<Action> playerInputActionsFromBits(final byte bits) {
    final EnumSet<Action> actions = EnumSet.noneOf(Action.class);
    for (final Action action : Action.values()) {
      assert action.ordinal() < 8;
      if ((bits & (1 << action.ordinal())) != 0) {
        actions.add(action);
      }
    }
    return actions;
  }

  private static EnumSet<UpdateFlag> entityUpdateFlagsFromBits(final byte bits) {
    final EnumSet<UpdateFlag> flags = EnumSet.noneOf(UpdateFlag.class);
    for (final UpdateFlag flag : UpdateFlag.values()) {
      assert flag.ordinal() < 8;
      if ((bits & (1 << flag.ordinal())) != 0) {
        flags.add(flag);
      }
    }
    return flags;
  }

  private static byte entityUpdateFlagsToBits(final EnumSet<UpdateFlag> flags) {
    byte bits = 0; 
    for (final UpdateFlag flag : flags) {
      assert flag.ordinal() < 8;
      bits |= (1 << flag.ordinal());
    }
    return bits;
  }
}
