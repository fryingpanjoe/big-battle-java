package org.fryingpanjoe.bigbattle.server.events;

import org.fryingpanjoe.bigbattle.common.networking.Packet;

public class ReceivedPacketFromClientEvent {

  public final int clientId;
  public final Packet packet;

  public ReceivedPacketFromClientEvent(final int clientId, final Packet packet) {
    this.clientId = clientId;
    this.packet = packet;
  }
}
