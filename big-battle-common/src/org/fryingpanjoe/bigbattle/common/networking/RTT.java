package org.fryingpanjoe.bigbattle.common.networking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RTT {

  private static final int MAX_RTT = 1000;

  private static class PacketTime {
    public final int id;
    public final int time;

    public PacketTime(final int id, final int time) {
      this.id = id;
      this.time = time;
    }
  }

  private int rtt;
  private List<PacketTime> packetTimes;

  public RTT() {
    this.rtt = -1;
    this.packetTimes = new ArrayList<>(64);
  }

  public void recordSentPacket(final int id, final int time) {
    this.packetTimes.add(new PacketTime(id, time));
  }

  public void recordReceivedPacket(final int id, final int time) {
    final Iterator<PacketTime> packetTimeIter = this.packetTimes.iterator();
    while (packetTimeIter.hasNext()) {
      final PacketTime packetTime = packetTimeIter.next();
      if (packetTime.id == id) {
        final int packetRtt = time - packetTime.time;
        if (packetRtt < MAX_RTT) {
          this.rtt += (packetRtt - this.rtt) / 10;
        }
        packetTimeIter.remove();
        break;
      }
    }
  }
}
