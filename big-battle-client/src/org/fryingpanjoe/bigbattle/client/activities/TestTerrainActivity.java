package org.fryingpanjoe.bigbattle.client.activities;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Random;

import org.fryingpanjoe.bigbattle.client.rendering.IsometricCamera;
import org.fryingpanjoe.bigbattle.client.rendering.TerrainRenderer;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainGenerator;
import org.fryingpanjoe.bigbattle.common.terrain.TerrainPatch;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;

public class TestTerrainActivity implements Activity {

  private final TerrainRenderer rendering;
  private final TerrainPatch patch;
  private final IsometricCamera camera;

  public TestTerrainActivity() throws IOException {
    this.rendering = new TerrainRenderer();
    this.patch = TerrainGenerator.generateRandomPatch(128, ImmutableList.of(0, 1), new Random());
    this.camera = new IsometricCamera();
    this.camera.setPos(64, 64);
  }

  @Override
  public boolean update() {
    return true;
  }

  @Override
  public void draw() {
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
  }

  @Override
  public void key(final int key, final char character, final boolean down) {
  }

  @Override
  public void mouseMove(final int x, final int y, final int dx, final int dy) {
  }
}
