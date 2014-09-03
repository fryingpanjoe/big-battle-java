package org.fryingpanjoe.bigbattle.client.events;

import org.fryingpanjoe.bigbattle.common.networking.Packet;

public class ReceivedPacketFromServerEvent {

  public final Packet packet;

  public ReceivedPacketFromServerEvent(final Packet packet) {
    this.packet = packet;
  }
}
