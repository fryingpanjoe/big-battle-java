package org.fryingpanjoe.bigbattle.common.network;

import org.fryingpanjoe.bigbattle.common.events.Event;
import org.fryingpanjoe.bigbattle.common.events.SynchronizedEvent;

import com.google.common.eventbus.Subscribe;

public class Protocol {

  private PacketWriter writer;

  public Protocol() {
  }

  @Subscribe
  public void onEvent(final Event event) {
    if (event instanceof SynchronizedEvent) {
      final SynchronizedEvent synchronizedEvent = (SynchronizedEvent) event;
      synchronizedEvent.writeTo(writer);
    }
  }
}
