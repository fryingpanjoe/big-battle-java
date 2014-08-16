package org.fryingpanjoe.bigbattle.common.events;

public class ClientConnectedEvent {

  public int clientId;

  public ClientConnectedEvent(final int clientId) {
    this.clientId = clientId;
  }
}
