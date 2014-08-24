package org.fryingpanjoe.bigbattle.client.activities;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Random;

import org.fryingpanjoe.bigbattle.client.rendering.IsometricCamera;
import org.fryingpanjoe.bigbattle.client.rendering.TerrainRendering;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainGenerator;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainPatch;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;

public class TestTerrainActivity implements Activity {

  private final IsometricCamera camera;
  private final TerrainRendering rendering;
  private final TerrainPatch patch;

  public TestTerrainActivity() throws IOException {
    this.camera = new IsometricCamera();
    this.rendering = new TerrainRendering();
    this.patch = TerrainGenerator.generateRandomPatch(128, ImmutableList.of(0, 1), new Random());
    this.camera.setPos(64, 64);
  }

  @Override
  public boolean update(int deltaTime) {
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
    this.rendering.renderTerrainPatch(this.camera, this.patch);

    return true;
  }

  @Override
  public void key(int key, char character, boolean down) {
  }

  @Override
  public void mouseButton(int button, boolean down) {
  }

  @Override
  public void mouseWheel(int delta) {
  }

  @Override
  public void mouseMove(int x, int y, int dx, int dy) {
  }
}