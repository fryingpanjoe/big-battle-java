package org.fryingpanjoe.bigbattle.server;

import java.util.ArrayList;
import java.util.List;

import org.fryingpanjoe.bigbattle.server.game.Area;

public class ServerAreaManager {

  private final List<Area> areas;

  public ServerAreaManager() {
    this.areas = new ArrayList<>();
  }
}
