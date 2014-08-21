package org.fryingpanjoe.bigbattle.client.activities;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.fryingpanjoe.bigbattle.client.ClientEntityManager;
import org.fryingpanjoe.bigbattle.client.ClientPlayerManager;
import org.fryingpanjoe.bigbattle.client.ClientTerrainManager;
import org.fryingpanjoe.bigbattle.client.game.ClientPlayer;
import org.fryingpanjoe.bigbattle.client.rendering.EntityRenderer;
import org.fryingpanjoe.bigbattle.client.rendering.IsometricCamera;
import org.fryingpanjoe.bigbattle.client.rendering.TerrainRenderer;
import org.fryingpanjoe.bigbattle.common.events.EnterGameEvent;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.google.common.eventbus.Subscribe;

public class MultiplayerActivity implements Activity {

  private final ClientEntityManager entityManager;
  private final ClientPlayerManager playerManager;
  private final ClientTerrainManager terrainManager;
  private final TerrainRenderer terrainRenderer;
  private final EntityRenderer entityRenderer;
  private final IsometricCamera camera;

  private int clientPlayerId;
  private ClientPlayer clientPlayer;

  public MultiplayerActivity(final ClientEntityManager entityManager,
                             final ClientPlayerManager playerManager,
                             final ClientTerrainManager terrainManager,
                             final TerrainRenderer terrainRenderer,
                             final EntityRenderer entityRenderer,
                             final IsometricCamera camera) throws IOException {
    this.entityManager = entityManager;
    this.playerManager = playerManager;
    this.terrainManager = terrainManager;
    this.terrainRenderer = terrainRenderer;
    this.entityRenderer = entityRenderer;
    this.camera = camera;
    this.clientPlayerId = -1;
  }

  @Subscribe
  public void onEnterGameEvent(final EnterGameEvent event) {
    this.clientPlayerId = event.playerId;
    if (this.clientPlayerId != -1) {
      this.clientPlayer = this.playerManager.getPlayers().get(this.clientPlayerId);
    }
  }

  @Override
  public boolean update(final long deltaTime) {
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

    if (this.clientPlayer == null && this.clientPlayerId != -1) {
      this.clientPlayer = this.playerManager.getPlayers().get(this.clientPlayerId);
    }

    if (this.clientPlayer != null) {
      this.terrainRenderer.renderTerrainPatch(
        this.camera,
        this.terrainManager.getTerrainPatch(
          this.clientPlayer.getClientEntity().getEntity().getPos().x,
          this.clientPlayer.getClientEntity().getEntity().getPos().y));
    }

    this.entityRenderer.renderEntities(
      this.camera, this.entityManager.getRenderEntities().values());

    return true;
  }

  @Override
  public void key(final int key, final char character, final boolean down) {
  }

  @Override
  public void mouseButton(final int button, final boolean down) {
  }

  @Override
  public void mouseWheel(final int delta) {
  }

  @Override
  public void mouseMove(final int x, final int y, final int dx, final int dy) {
  }
}
