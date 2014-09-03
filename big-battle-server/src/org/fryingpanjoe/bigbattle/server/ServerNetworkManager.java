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
import org.fryingpanjoe.bigbattle.server.events.ReceivedPacketFromClientEvent;
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

    private long timeout;

    public Client(final int id, final Channel channel, final SocketAddress address) {
      this.id = id;
      this.channel = channel;
      this.address = address;
      this.timeout = Sys.getTime();
    }

    public void resetTimeout() {
      this.timeout = Sys.getTime();
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

    public boolean isTimeout() {
      return (Sys.getTime() - this.timeout) >= TIMEOUT_MS;
    }
  }

  private final EventBus eventBus;
  private final List<Client> clients;
  private final List<Integer> disconnectedClients;
  private DatagramChannel socket;
  private int clientIdGenerator;

  public ServerNetworkManager(final EventBus eventBus) {
    this.eventBus = eventBus;
    this.clients = new ArrayList<>();
    this.disconnectedClients = new ArrayList<>();
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

  public void disconnectClient(final int clientId) {
    this.disconnectedClients.add(clientId);
  }

  public void checkTimeouts() {
    final Iterator<Client> clientIterator = this.clients.iterator();
    while (clientIterator.hasNext()) {
      final Client client = clientIterator.next();
      if (client.isTimeout()) {
        LOG.info("Client timed out: " + client.getId());
        clientIterator.remove();
        this.eventBus.post(new ClientDisconnectedEvent(client.getId()));
      }
    }
  }

  public void removeDisconnectedClients() {
    final Iterator<Integer> clientIdIterator = this.disconnectedClients.iterator();
    while (clientIdIterator.hasNext()) {
      final int clientId = clientIdIterator.next();
      final Iterator<Client> clientIterator = this.clients.iterator();
      while (clientIterator.hasNext()) {
        final Client client = clientIterator.next();
        if (client.getId() == clientId) {
          LOG.info("Client disconnected: " + client.getId());
          clientIterator.remove();
          this.eventBus.post(new ClientDisconnectedEvent(client.getId()));
        }
      }
      clientIdIterator.remove();
    }
  }

  public void receivePacketsFromClients() {
    while (true) {
      try {
        final ByteBuffer receivedData = Channel.createPacketBuffer();
        final SocketAddress address = this.socket.receive(receivedData);
        if (address != null) {
          receivedData.flip();
          boolean found = false;
          final Iterator<Client> clientIterator = this.clients.iterator();
          while (clientIterator.hasNext()) {
            final Client client = clientIterator.next();
            if (client.getAddress().equals(address)) {
              found = true;
              final Packet packet = client.getChannel().onDataReceived(receivedData);
              if (packet != null) {
                client.resetTimeout();
                this.eventBus.post(new ReceivedPacketFromClientEvent(client.getId(), packet));
              }
              break;
            }
          }
          if (!found) {
            final Channel channel = new Channel(this.socket, address);
            final Packet packet = channel.onDataReceived(receivedData);
            final ByteBuffer packetDataBuffer = ByteBuffer.wrap(packet.getData());
            final Protocol.PacketType packetType = Protocol.readPacketHeader(packetDataBuffer);
            if (packetType == Protocol.PacketType.Hello) {
              final Client client = new Client(getNextClientId(), channel, address);
              this.clients.add(client);
              LOG.info(
                String.format(
                  "Client %d connected (%s)",
                  client.getId(), ((InetSocketAddress) address).getHostString()));
              this.eventBus.post(new ClientConnectedEvent(client.getId()));
            }
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

  private int getNextClientId() {
    return this.clientIdGenerator++;
  }
}
