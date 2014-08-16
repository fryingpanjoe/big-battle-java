package org.fryingpanjoe.bigbattle.client.rendering;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.fryingpanjoe.bigbattle.common.io.Files;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class Shaders {

  private static final int MAX_INFO_LOG_LENGTH = 8192;

  private static enum ShaderType {
    VertexShader,
    FragmentShader,
    GeometryShader
  }

  private static Map<ShaderType, Integer> SHADER_TO_GL_TYPE = ImmutableMap.of(
    ShaderType.VertexShader, GL20.GL_VERTEX_SHADER,
    ShaderType.FragmentShader, GL20.GL_FRAGMENT_SHADER,
    ShaderType.GeometryShader, GL32.GL_GEOMETRY_SHADER);

  private Shaders() {
  }

  public static int getProgram(
      final String vertexShaderFilename,
      final String fragmentShaderFilename) throws IOException {
    return getProgram(vertexShaderFilename, fragmentShaderFilename, "");
  }

  public static int getProgram(
      final String vertexShaderFilename,
      final String fragmentShaderFilename,
      final String geometryShaderFilename) throws IOException {
    final List<Integer> shaders = Lists.newArrayListWithCapacity(3);
    if (!vertexShaderFilename.isEmpty()) {
      shaders.add(compile(readFileAsString(vertexShaderFilename), ShaderType.VertexShader));
    }
    if (!fragmentShaderFilename.isEmpty()) {
      shaders.add(compile(readFileAsString(fragmentShaderFilename), ShaderType.FragmentShader));
    }
    if (!geometryShaderFilename.isEmpty()) {
      shaders.add(compile(readFileAsString(geometryShaderFilename), ShaderType.GeometryShader));
    }
    return link(shaders);
  }

  private static int link(final List<Integer> shaders) {
    final int program = GL20.glCreateProgram();
    if (program == 0) {
      throw new RuntimeException("Failed to create program");
    }
    for (final int shader : shaders) {
      GL20.glAttachShader(program, shader);
    }
    //GL20.glBindAttribLocation(this.shader, 0, "in_Position");
    //GL20.glBindAttribLocation(this.shader, 1, "in_TexCoord");
    //GL20.glBindAttribLocation(this.shader, 2, "in_Normal");
    GL20.glLinkProgram(program);
    final int linkStatus = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);
    if (linkStatus == GL11.GL_FALSE) {
      final String linkLog = GL20.glGetProgramInfoLog(program, MAX_INFO_LOG_LENGTH);
      throw new RuntimeException(String.format("Failed to link program: %s", linkLog));
    }
    GL20.glValidateProgram(program);
    final int validateStatus = GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS);
    if (validateStatus == GL11.GL_FALSE) {
      final String validateLog = GL20.glGetProgramInfoLog(program, MAX_INFO_LOG_LENGTH);
      throw new RuntimeException(String.format("Failed to validate program: %s", validateLog));
    }
    return program;
  }

  private static int compile(final String sourceCode, final ShaderType shaderType) {
    final int shader = GL20.glCreateShader(SHADER_TO_GL_TYPE.get(shaderType));
    if (shader == 0) {
      throw new RuntimeException(
        String.format("Failed to create shader: %s", shaderType.name()));
    }
    try {
      GL20.glShaderSource(shader, sourceCode);
      GL20.glCompileShader(shader);
      final int compileStatus = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
      if (compileStatus == GL11.GL_FALSE) {
        final String compileLog = GL20.glGetShaderInfoLog(shader, MAX_INFO_LOG_LENGTH);
        throw new RuntimeException(String.format("Failed to compile shader: %s", compileLog));
      }
      return shader;
    } catch (final Exception e) {
      GL20.glDeleteShader(shader);
      throw e;
    }
  }

  private static String readFileAsString(final String filename) throws IOException {
    return Files.getFileAsString(filename);
  }
}
