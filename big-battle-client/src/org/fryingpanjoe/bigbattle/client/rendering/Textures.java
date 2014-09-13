package org.fryingpanjoe.bigbattle.client.rendering;

import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.fryingpanjoe.bigbattle.common.io.Files;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Textures {

  private Textures() {
  }

  public static Texture getTexture(final String filename) throws IOException {
    final Texture texture = TextureLoader.getTexture(
      FilenameUtils.getExtension(filename).toUpperCase(), Files.getFileAsStream(filename));
    texture.bind();
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    return texture;
  }
}
