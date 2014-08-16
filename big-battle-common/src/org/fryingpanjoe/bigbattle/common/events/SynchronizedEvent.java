package org.fryingpanjoe.bigbattle.common.events;

import org.fryingpanjoe.bigbattle.common.networking.PacketReader;
import org.fryingpanjoe.bigbattle.common.networking.PacketWriter;

public interface SynchronizedEvent extends Event {

  void writeTo(PacketWriter writer);

  void readFrom(PacketReader reader);
}
