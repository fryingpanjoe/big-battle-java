package org.fryingpanjoe.bigbattle.server.events;

public class ClientDisconnectedEvent {

  public final int clientId;

  public ClientDisconnectedEvent(final int clientId) {
    this.clientId = clientId;
  }
}
