package org.fryingpanjoe.bigbattle.server.config;

import static com.google.common.base.Preconditions.checkNotNull;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ServerConfig {

  private static final String DEFAULT_CONFIG_NAME = "big-battle-server";

  private static final String DEFAULT_BIND_ADDRESS = "localhost";
  private static final int DEFAULT_BIND_PORT = 12345;
  private static final double DEFAULT_MAX_FPS = 10.0;

  private final String bindAddress;
  private final int bindPort;
  private final double maxFps;

  public ServerConfig() {
    this(DEFAULT_CONFIG_NAME);
  }

  public ServerConfig(final String name) {
    this(ConfigFactory.load(checkNotNull(name)));
  }

  public ServerConfig(final Config config) {
    final Config serverConfig = checkNotNull(config).getConfig("big-battle-server");
    if (serverConfig.hasPath("net.bind-address")) {
      this.bindAddress = serverConfig.getString("net.bind-address");
    } else {
      this.bindAddress = DEFAULT_BIND_ADDRESS;
    }
    if (serverConfig.hasPath("net.bind-port")) {
      this.bindPort = serverConfig.getInt("net.bind-port");
    } else {
      this.bindPort = DEFAULT_BIND_PORT;
    }
    if (serverConfig.hasPath("max-fps")) {
      this.maxFps = serverConfig.getDouble("max-fps");
    } else {
      this.maxFps = DEFAULT_MAX_FPS;
    }
  }

  public String getBindAddress() {
    return this.bindAddress;
  }

  public int getBindPort() {
    return this.bindPort;
  }

  public double getMaxFps() {
    return this.maxFps;
  }
}
