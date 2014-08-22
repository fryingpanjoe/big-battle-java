package org.fryingpanjoe.bigbattle.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.common.networking.Channel;
import org.fryingpanjoe.bigbattle.common.networking.Packet;
import org.fryingpanjoe.bigbattle.common.networking.Protocol;
import org.fryingpanjoe.bigbattle.server.events.ClientConnectedEvent;
import org.fryingpanjoe.bigbattle.server.events.ClientDisconnectedEvent;
import org.fryingpanjoe.bigbattle.server.events.ServerOfflineEvent;
import org.fryingpanjoe.bigbattle.server.events.ServerOnlineEvent;
import org.lwjgl.Sys;

import com.google.common.eventbus.EventBus;

public class ServerNetworkManager {

  private static final Logger LOG = Logger.getLogger(ServerNetworkManager.class.getName());

  private static final long TIMEOUT_MS = 10_000;

  private static class Client {

    private final int id;
    private final Channel channel;
    private final SocketAddress address;

    private long receivedPacketAt;
    private int frame;

    public Client(final int id, final Channel channel, final SocketAddress address) {
      this.id = id;
      this.channel = channel;
      this.address = address;
      this.receivedPacketAt = 0;
      this.frame = -1;
    }

    public void setReceivedPacketAt(final long receivedPacketAt) {
      this.receivedPacketAt = receivedPacketAt;
    }

    public void setFrame(final int frame) {
      this.frame = frame;
    }

    public int getId() {
      return this.id;
    }

    public Channel getChannel() {
      return this.channel;
    }

    public SocketAddress getAddress() {
      return this.address;
    }

    public long getReceivedPacketAt() {
      return this.receivedPacketAt;
    }

    public int getFrame() {
      return this.frame;
    }
  }

  private final EventBus eventBus;
  private final List<Client> clients;
  private DatagramChannel socket;
  private int clientIdGenerator;

  public ServerNetworkManager(final EventBus eventBus) {
    this.eventBus = eventBus;
    this.clients = new ArrayList<>();
    this.socket = null;
    this.clientIdGenerator = 1;
  }

  public void bind(final String bindHost, final int bindPort) throws IOException {
    final InetSocketAddress bindAddress = new InetSocketAddress(
      InetAddress.getByName(bindHost), bindPort);
    LOG.info("Server listening on " + bindAddress.getHostString() + ":" + bindAddress.getPort());
    this.socket = DatagramChannel.open();
    this.socket.configureBlocking(false);
    this.socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
    this.socket.bind(bindAddress);
    this.eventBus.post(new ServerOnlineEvent());
  }

  public void shutdown() {
    this.clients.clear();
    if (this.socket != null && this.socket.isOpen()) {
      LOG.info("Closing server socket");
      try {
        this.socket.close();
      } catch (final IOException e) {
        // ignore?
      }
      this.socket = null;
      this.eventBus.post(new ServerOfflineEvent());
    }
  }

  public void checkTimeouts() {
    final long now = Sys.getTime();
    final Iterator<Client> clientIterator = this.clients.iterator();
    while (clientIterator.hasNext()) {
      final Client client = clientIterator.next();
      if ((now - client.getReceivedPacketAt()) >= TIMEOUT_MS) {
        LOG.info("Client timed out: " + client.getId());
        clientIterator.remove();
        this.eventBus.post(new ClientDisconnectedEvent(client.getId()));
      }
    }
  }

  public void receivePacketsFromClients() {
    while (true) {
      try {
        final ByteBuffer receivedData = Channel.createPacketBuffer();
        final SocketAddress address = this.socket.receive(receivedData);
        if (address != null) {
          receivedData.flip();
          Client fromClient = null;
          for (final Client client : this.clients) {
            if (client.getAddress().equals(address)) {
              fromClient = client;
              break;
            }
          }
          if (fromClient == null) {
            LOG.info("Client connected: " + ((InetSocketAddress) address).getHostString());
            fromClient = new Client(
              this.clientIdGenerator, new Channel(this.socket, address), address);
            ++this.clientIdGenerator;
            this.clients.add(fromClient);
            this.eventBus.post(new ClientConnectedEvent(fromClient.getId()));
          }
          final Packet packet = fromClient.getChannel().onDataReceived(receivedData);
          if (packet != null) {
            onPacketReceivedFromClient(fromClient.getId(), packet);
            fromClient.setReceivedPacketAt(Sys.getTime());
          }
        } else {
          break;
        }
      } catch (final IOException e) {
        LOG.warning("Failed to receive packet: " + e.getMessage());
        break;
      }
    }
  }

  public void sendPacketTo(final int clientId, final ByteBuffer packet) {
    packet.flip();
    final Iterator<Client> clientIterator = this.clients.iterator();
    while (clientIterator.hasNext()) {
      final Client client = clientIterator.next();
      if (client.getId() == clientId) {
        try {
          client.getChannel().sendPacket(packet);
        } catch (final IOException e) {
          LOG.warning(String.format("Failed to send packet to client %d: %s", client.getId(), e));
          clientIterator.remove();
          this.eventBus.post(new ClientDisconnectedEvent(client.getId()));
        }
        break;
      }
    }
  }

  public void sendPacketToAll(final ByteBuffer packet) {
    packet.flip();
    final Iterator<Client> clientIterator = this.clients.iterator();
    while (clientIterator.hasNext()) {
      final Client client = clientIterator.next();
      try {
        client.getChannel().sendPacket(packet);
        packet.rewind();
      } catch (final IOException e) {
        LOG.warning(String.format("Failed to send packet to client %d: %s", client.getId(), e));
        clientIterator.remove();
        this.eventBus.post(new ClientDisconnectedEvent(client.getId()));
      }
    }
  }

  private void onPacketReceivedFromClient(final int clientId, final Packet packet) {
    final ByteBuffer data = ByteBuffer.wrap(packet.getData());
    final Protocol.PacketType packetType = Protocol.readPacketHeader(data);
    // TODO parse packet based on type
    // TODO post stuff to eventBus
  }
}
