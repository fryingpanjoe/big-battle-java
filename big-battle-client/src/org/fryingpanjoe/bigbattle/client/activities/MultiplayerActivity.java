package org.fryingpanjoe.bigbattle.client.activities;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.fryingpanjoe.bigbattle.client.ClientEntityManager;
import org.fryingpanjoe.bigbattle.client.ClientNetworkManager;
import org.fryingpanjoe.bigbattle.client.ClientPlayerManager;
import org.fryingpanjoe.bigbattle.client.ClientTerrainManager;
import org.fryingpanjoe.bigbattle.client.Keybinding;
import org.fryingpanjoe.bigbattle.client.UpdateRateTimer;
import org.fryingpanjoe.bigbattle.client.events.ConnectedToServerEvent;
import org.fryingpanjoe.bigbattle.client.game.ClientPlayer;
import org.fryingpanjoe.bigbattle.client.rendering.EntityRenderer;
import org.fryingpanjoe.bigbattle.client.rendering.IsometricCamera;
import org.fryingpanjoe.bigbattle.client.rendering.TerrainRenderer;
import org.fryingpanjoe.bigbattle.common.events.EnterGameEvent;
import org.fryingpanjoe.bigbattle.common.events.NoticePlayerEvent;
import org.fryingpanjoe.bigbattle.common.game.PlayerInput;
import org.fryingpanjoe.bigbattle.common.networking.Channel;
import org.fryingpanjoe.bigbattle.common.networking.Protocol;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class MultiplayerActivity implements Activity {

  private final EventBus eventBus;
  private final ClientNetworkManager networkManager;
  private final ClientEntityManager entityManager;
  private final ClientPlayerManager playerManager;
  private final ClientTerrainManager terrainManager;
  private final TerrainRenderer terrainRenderer;
  private final EntityRenderer entityRenderer;
  private final Keybinding keybinding;

  private final IsometricCamera camera;
  private final PlayerInput playerInput;
  private final UpdateRateTimer inputRate;

  private int clientPlayerId;
  private ClientPlayer clientPlayer;

  public MultiplayerActivity(final EventBus eventBus,
                             final ClientNetworkManager networkManager,
                             final ClientEntityManager entityManager,
                             final ClientPlayerManager playerManager,
                             final ClientTerrainManager terrainManager,
                             final TerrainRenderer terrainRenderer,
                             final EntityRenderer entityRenderer,
                             final Keybinding keybinding) throws IOException {
    this.eventBus = eventBus;
    this.networkManager = networkManager;
    this.entityManager = entityManager;
    this.playerManager = playerManager;
    this.terrainManager = terrainManager;
    this.terrainRenderer = terrainRenderer;
    this.entityRenderer = entityRenderer;
    this.keybinding = keybinding;
    // internal state
    this.camera = new IsometricCamera();
    this.playerInput = new PlayerInput();
    this.inputRate = UpdateRateTimer.fromFps(30.f);
    // mutable state
    this.clientPlayerId = -1;
    this.clientPlayer = null;
  }

  @Subscribe
  public void onConnectedToServerEvent(final ConnectedToServerEvent event) {
    // send hello
  }

  @Subscribe
  public void onEnterGameEvent(final EnterGameEvent event) {
    this.clientPlayerId = event.playerId;
    if (this.clientPlayerId != -1) {
      this.clientPlayer = this.playerManager.getPlayers().get(this.clientPlayerId);
    }
  }

  @Subscribe
  public void onNoticePlayerEvent(final NoticePlayerEvent event) {
    if (this.clientPlayer == null && this.clientPlayerId != -1) {
      this.clientPlayer = this.playerManager.getPlayers().get(this.clientPlayerId);
    }
  }

  @Override
  public boolean update() {
    this.networkManager.receivePacketFromServer();
    if (this.inputRate.shouldUpdate()) {
      final ByteBuffer packet = Channel.createPacketBuffer();
      Protocol.writePacketHeader(packet, Protocol.PacketType.PlayerInput);
      Protocol.writePlayerInput(packet, this.playerInput);
      packet.flip();
      this.networkManager.sendPacketToServer(packet);
    }
    return true;
  }

  @Override
  public void draw() {
    if (this.clientPlayer != null) {
      this.camera.setPos(this.clientPlayer.getClientEntity().getEntity().getPos());
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

    if (this.clientPlayer != null) {
      this.terrainRenderer.renderTerrainPatch(
        this.camera,
        this.terrainManager.getTerrainPatch(
          this.clientPlayer.getClientEntity().getEntity().getPos().x,
          this.clientPlayer.getClientEntity().getEntity().getPos().y));
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
}
