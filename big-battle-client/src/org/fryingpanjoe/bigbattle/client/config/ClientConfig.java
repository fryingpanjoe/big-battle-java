package org.fryingpanjoe.bigbattle.client.config;

import static com.google.common.base.Preconditions.checkNotNull;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ClientConfig {

  private static String DEFAULT_CONFIG_NAME = "big-battle-client";

  private final int displayWidth;
  private final int displayHeight;
  private final boolean displayFullscreen;
  private final boolean displayVsync;

  public ClientConfig() {
    this(DEFAULT_CONFIG_NAME);
  }

  public ClientConfig(final String name) {
    this(ConfigFactory.load(checkNotNull(name)));
  }

  public ClientConfig(final Config config) {
    final Config clientConfig = checkNotNull(config).getConfig("big-battle-client");
    this.displayWidth = clientConfig.getInt("display.width");
    this.displayHeight = clientConfig.getInt("display.height");
    this.displayFullscreen = clientConfig.getBoolean("display.fullscreen");
    this.displayVsync = clientConfig.getBoolean("display.vsync");
  }

  public int getDisplayWidth() {
    return this.displayWidth;
  }

  public int getDisplayHeight() {
    return this.displayHeight;
  }

  public boolean getDisplayFullscreen() {
    return this.displayFullscreen;
  }

  public boolean getDisplayVsync() {
    return this.displayVsync;
  }
}
