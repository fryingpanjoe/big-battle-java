package org.fryingpanjoe.bigbattle.server.events;

public class ClientConnectedEvent {

  public final int clientId;

  public ClientConnectedEvent(final int clientId) {
    this.clientId = clientId;
  }
}
