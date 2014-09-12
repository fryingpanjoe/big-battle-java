package org.fryingpanjoe.bigbattle.client.activities;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.fryingpanjoe.bigbattle.client.ClientEntityManager;
import org.fryingpanjoe.bigbattle.client.ClientNetworkManager;
import org.fryingpanjoe.bigbattle.client.Keybinding;
import org.fryingpanjoe.bigbattle.client.UpdateRateTimer;
import org.fryingpanjoe.bigbattle.client.game.ClientEntity;
import org.fryingpanjoe.bigbattle.client.rendering.EntityRenderer;
import org.fryingpanjoe.bigbattle.client.rendering.IsometricCamera;
import org.fryingpanjoe.bigbattle.client.rendering.TerrainRenderer;
import org.fryingpanjoe.bigbattle.common.game.Entity;
import org.fryingpanjoe.bigbattle.common.game.PlayerInput;
import org.fryingpanjoe.bigbattle.common.game.PlayerInputController;
import org.fryingpanjoe.bigbattle.common.networking.Channel;
import org.fryingpanjoe.bigbattle.common.networking.Protocol;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class MultiplayerActivity implements Activity {

  private final int clientId;
  private final int entityId;
  private final ClientNetworkManager networkManager;
  private final ClientEntityManager entityManager;
  private final TerrainRenderer terrainRenderer;
  private final EntityRenderer entityRenderer;
  private final Keybinding keybinding;

  private final IsometricCamera camera;
  private final PlayerInput playerInput;
  private final UpdateRateTimer inputRate;

  public MultiplayerActivity(final int clientId,
                             final int entityId,
                             final ClientNetworkManager networkManager,
                             final ClientEntityManager entityManager,
                             final TerrainRenderer terrainRenderer,
                             final EntityRenderer entityRenderer,
                             final Keybinding keybinding) throws IOException {
    this.clientId = clientId;
    this.entityId = entityId;
    this.networkManager = networkManager;
    this.entityManager = entityManager;
    this.terrainRenderer = terrainRenderer;
    this.entityRenderer = entityRenderer;
    this.keybinding = keybinding;
    // internal state
    this.camera = new IsometricCamera();
    this.playerInput = new PlayerInput();
    this.inputRate = UpdateRateTimer.fromFps(30.f);
  }

  @Override
  public boolean update(final float deltaTime) {
    final ClientEntity playerEntity = this.entityManager.getEntities().get(this.entityId);
    if (playerEntity != null) {
      PlayerInputController.respondToPlayerInput(playerEntity.getEntity(), this.playerInput);
    }
    this.entityManager.updatePositions(deltaTime);
    if (this.inputRate.shouldUpdate()) {
      //sendPlayerInputToServer(this.playerInput);
      sendPlayerEntityToServer(playerEntity.getEntity());
    }
    return true;
  }

  @Override
  public void draw() {
    final ClientEntity playerEntity = this.entityManager.getEntities().get(this.entityId);
    if (playerEntity != null) {
      this.camera.setPosition(
        playerEntity.getEntity().getX(),
        playerEntity.getEntity().getY());
    }

    GL11.glClearColor(0.f, 0.f, 0.f, 0.f);
    GL11.glClearDepth(1.f);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

    this.camera.setupGL();

    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glCullFace(GL11.GL_BACK);
    GL11.glFrontFace(GL11.GL_CCW);

    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_LIGHT0);
    final FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(4);
    positionBuffer.put(new float[] {0.2f, 1.f, -0.2f, 0.f});
    positionBuffer.flip();
    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, positionBuffer);

    if (playerEntity != null) {
      this.terrainRenderer.renderTerrain(this.camera);
    }

    this.entityRenderer.renderEntities(
      this.camera, this.entityManager.getRenderEntities().values());
  }

  @Override
  public void key(final int key, final char character, final boolean down) {
    final PlayerInput.Action action = this.keybinding.get(key);
    if (action != null) {
      this.playerInput.setAction(action, down);
    }
  }

  @Override
  public void mouseMove(final int x, final int y, final int dx, final int dy) {
  }

  private void sendPlayerInputToServer(final PlayerInput playerInput) {
    final ByteBuffer packet = Channel.createPacketBuffer();
    Protocol.writePacketHeader(packet, Protocol.PacketType.PlayerInput);
    Protocol.writePlayerInput(packet, playerInput);
    packet.flip();
    this.networkManager.sendPacketToServer(packet);
  }

  private void sendPlayerEntityToServer(final Entity entity) {
    final ByteBuffer packet = Channel.createPacketBuffer();
    Protocol.writePacketHeader(packet, Protocol.PacketType.EntityDelta);
    entity.getUpdateFlags().remove(Entity.UpdateFlag.Velocity);
    Protocol.writeEntityDelta(packet, entity);
    packet.flip();
    this.networkManager.sendPacketToServer(packet);
  }
}
