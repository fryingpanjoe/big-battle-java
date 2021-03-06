package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.fryingpanjoe.bigbattle.client.activities.Activity;
import org.fryingpanjoe.bigbattle.client.activities.MultiplayerActivity;
import org.fryingpanjoe.bigbattle.client.config.ClientConfig;
import org.fryingpanjoe.bigbattle.client.events.ConnectedEvent;
import org.fryingpanjoe.bigbattle.client.events.DisconnectedEvent;
import org.fryingpanjoe.bigbattle.client.events.ReceivedPacketFromServerEvent;
import org.fryingpanjoe.bigbattle.client.rendering.OpenGLDefaults;
import org.fryingpanjoe.bigbattle.client.rendering.EntityRenderer;
import org.fryingpanjoe.bigbattle.client.rendering.TerrainRenderer;
import org.fryingpanjoe.bigbattle.common.events.EnterGameEvent;
import org.fryingpanjoe.bigbattle.common.game.PlayerInput;
import org.fryingpanjoe.bigbattle.common.networking.Channel;
import org.fryingpanjoe.bigbattle.common.networking.Protocol;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class Main {

  private static final Logger LOG = Logger.getLogger(Main.class.getName());

  private static final String TITLE = "Big Battle Client";

  public static void main(final String[] argv) throws SecurityException, IOException {
    final FileHandler handler = new FileHandler("client.log");
    handler.setFormatter(new SimpleFormatter());
    Logger.getLogger("").addHandler(handler);
    try {
      LOG.info("Client starting");

      final ClientConfig config = new ClientConfig();

      LOG.info("Initializing display");
      Display.setTitle(TITLE);
      Display.setDisplayMode(new DisplayMode(config.getDisplayWidth(), config.getDisplayHeight()));
      Display.setFullscreen(config.getDisplayFullscreen());
      Display.setVSyncEnabled(config.getDisplayVsync());
      Display.create();

      OpenGLDefaults.setupGLInvariants();
      OpenGLDefaults.setupViewportFromDisplay();

      final EventBus eventBus = new EventBus();
      final ClientNetworkManager networkManager = new ClientNetworkManager(eventBus);
      final ClientEntityManager entityManager = new ClientEntityManager();
      eventBus.register(entityManager);
      final ClientTerrainManager terrainManager = new ClientTerrainManager();
      final TerrainRenderer terrainRenderer = new TerrainRenderer(terrainManager);
      final EntityRenderer entityRenderer = new EntityRenderer();
      final Keybinding keybinding = new Keybinding();
      for (final Map.Entry<String, String> bind : config.getBindings().entrySet()) {
        keybinding.bind(
          Keybinding.getKeyForName(bind.getKey()),
          PlayerInput.Action.valueOf(bind.getValue()));
      }

      final List<Activity> activityStack = new LinkedList<>();

      final Object eventHandler = new Object() {

        @Subscribe
        public void onRecievedPacketFromServerEvent(final ReceivedPacketFromServerEvent event)
            throws IOException {
          final ByteBuffer data = ByteBuffer.wrap(event.packet.getData());
          final Protocol.PacketType packetType = Protocol.readPacketHeader(data);
          switch (packetType) {
            case EnterGameEvent:
              onEnterGameEvent(Protocol.readEnterGameEvent(data));
              break;

            case EntityNoticedEvent:
              eventBus.post(Protocol.readEntityNoticedEvent(data));
              break;

            case EntityLostEvent:
              eventBus.post(Protocol.readEntityLostEvent(data));
              break;

            default:
              LOG.info("Ignoring packet: " + packetType);
          }
        }

        public void onEnterGameEvent(final EnterGameEvent event) throws IOException {
          LOG.info("Entering game");
          final Activity activity = new MultiplayerActivity(
            event.clientId,
            event.entityId,
            networkManager,
            entityManager,
            terrainRenderer,
            entityRenderer,
            keybinding);
          activityStack.add(0, activity);
        }

        @Subscribe
        public void onConnectedEvent(final ConnectedEvent event) {
          LOG.info("Connected to server, sending hello");
          final ByteBuffer packet = Channel.createPacketBuffer();
          Protocol.writePacketHeader(packet, Protocol.PacketType.Hello);
          packet.flip();
          networkManager.sendPacketToServer(packet);
        }

        @Subscribe
        public void onDisconnectedEvent(final DisconnectedEvent event) {
          LOG.info("Disconnected from server");
          if (!activityStack.isEmpty()) {
            eventBus.unregister(activityStack.remove(0));
          }
        }
      };
      eventBus.register(eventHandler);

      final String host;
      if (argv.length > 0) {
        host = argv[0];
      } else {
        host = "localhost";
      }
      final int port;
      if (argv.length > 1) {
        port = Integer.parseInt(argv[1]);
      } else {
        port = 12345;
      }
      LOG.info("Connecting to " + host + ":" + port);
      networkManager.connect(host, port);

      int mouseWheel = 0;

      final FpsCounter fpsCounter = new FpsCounter();

      long lastUpdateTime = Sys.getTime();

      while (!Display.isCloseRequested()) {
        networkManager.receivePacketFromServer();
        if (!activityStack.isEmpty()) {
          final Activity activity = activityStack.get(0);
          while (Keyboard.next()) {
            activity.key(
              Keyboard.getEventKey(), Keyboard.getEventCharacter(), Keyboard.getEventKeyState());
          }
          while (Mouse.next()) {
            if (Mouse.getEventButton() != -1) {
              activity.key(
                Keyboard.KEYBOARD_SIZE + Mouse.getEventButton(), '\0', Mouse.getEventButtonState());
            }
            if (Mouse.getEventDWheel() != mouseWheel) {
              final int key = Mouse.getEventDWheel() < 0 ?
                Keybinding.MWHEELDOWN : Keybinding.MWHEELUP;
              if (Mouse.getEventDWheel() == 0) {
                activity.key(key, '\0', true);
              } else {
                activity.key(key, '\0', false);
              }
              mouseWheel = Mouse.getEventDWheel();
            }
            activity.mouseMove(
              Mouse.getEventX(), Mouse.getEventY(), Mouse.getEventDX(), Mouse.getEventDY());
          }
          final long now = Sys.getTime();
          final long timeSinceLastUpdate = now - lastUpdateTime;
          lastUpdateTime = now;
          final float deltaTime = (float) timeSinceLastUpdate / 1000.f;
          if (!activity.update(deltaTime)) {
            break;
          }
          activity.draw();
        }
        Display.update();
        if (config.getDisplayFps().isPresent()) {
          Display.sync(config.getDisplayFps().get());
        }
        if (fpsCounter.update()) {
          Display.setTitle(String.format("%s (%.2f FPS)", TITLE, fpsCounter.getFps()));
        }
      }

      // say goodbye
      final ByteBuffer packet = Channel.createPacketBuffer();
      Protocol.writePacketHeader(packet, Protocol.PacketType.Goodbye);
      packet.flip();
      networkManager.sendPacketToServer(packet);
      networkManager.disconnect();
    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final LWJGLException e) {
      e.printStackTrace();
    } finally {
      Display.destroy();
    }
    LOG.info("Client stopped");
  }
}
