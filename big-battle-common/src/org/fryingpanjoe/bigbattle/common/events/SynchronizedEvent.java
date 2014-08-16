package org.fryingpanjoe.bigbattle.common.events;

import org.fryingpanjoe.bigbattle.common.network.PacketReader;
import org.fryingpanjoe.bigbattle.common.network.PacketWriter;

public interface SynchronizedEvent extends Event {

  void writeTo(PacketWriter writer);

  void readFrom(PacketReader reader);
}
