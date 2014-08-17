package org.fryingpanjoe.bigbattle.common.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Files {

  private static final int CHUNK_SIZE = 1024;

  public static InputStream getFileAsStream(final String filename) throws IOException {
    final Path path = Paths.get("bin/" + filename);
    if (java.nio.file.Files.exists(path)) {
      return java.nio.file.Files.newInputStream(path);
    } else {
      return Files.class.getResourceAsStream(filename);
    }
  }

  public static String getFileAsString(final String filename) throws IOException {
    final BufferedReader reader = new BufferedReader(
      new InputStreamReader(getFileAsStream(filename), "UTF-8"));
    try {
      final StringBuilder text = new StringBuilder();
      final char[] chunk = new char[CHUNK_SIZE];
      while (reader.ready()) {
        final int bytesRead = reader.read(chunk, 0, chunk.length);
        if (bytesRead > 0) {
          text.append(chunk, 0, bytesRead);
        } else if (bytesRead == -1) {
          break;
        }
      }
      return text.toString();
    } finally {
      reader.close();
    }
  }
}
