package org.fryingpanjoe.bigbattle.client.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Map;

import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ClientConfig {

  private static final String DEFAULT_CONFIG_NAME = "big-battle-client";

  private final int displayWidth;
  private final int displayHeight;
  private final boolean displayFullscreen;
  private final boolean displayVsync;
  private final Optional<Integer> displayFps;
  private final Map<String, String> bindings;

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
    if (clientConfig.hasPath("display.fps")) {
      this.displayFps = Optional.of(clientConfig.getInt("display.fps"));
    } else {
      this.displayFps = Optional.absent();
    }
    if (clientConfig.hasPath("input.bind")) {
      this.bindings = Maps.transformValues(
        clientConfig.getObject("input.bind").unwrapped(), Functions.toStringFunction());
    } else {
      this.bindings = Collections.emptyMap();
    }
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

  public Optional<Integer> getDisplayFps() {
    return this.displayFps;
  }

  public Map<String, String> getBindings() {
    return this.bindings;
  }
}
