package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.client.activities.Activity;
import org.fryingpanjoe.bigbattle.client.activities.MultiplayerActivity;
import org.fryingpanjoe.bigbattle.client.config.ClientConfig;
import org.fryingpanjoe.bigbattle.client.events.ConnectedEvent;
import org.fryingpanjoe.bigbattle.client.events.DisconnectedEvent;
import org.fryingpanjoe.bigbattle.client.rendering.Defaults;
import org.fryingpanjoe.bigbattle.client.rendering.EntityRenderer;
import org.fryingpanjoe.bigbattle.client.rendering.TerrainRenderer;
import org.fryingpanjoe.bigbattle.common.events.EnterGameEvent;
import org.fryingpanjoe.bigbattle.common.game.PlayerInput;
import org.fryingpanjoe.bigbattle.common.networking.Channel;
import org.fryingpanjoe.bigbattle.common.networking.Protocol;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class Main {

  private static final Logger LOG = Logger.getLogger(Main.class.getName());

  private static final String TITLE = "Big Battle Client";

  public static void main(final String[] argv) {
    try {
      LOG.info("Starting big-battle-client");

      final ClientConfig config = new ClientConfig();

      LOG.info("Initializing display");
      Display.setTitle(TITLE);
      Display.setDisplayMode(new DisplayMode(config.getDisplayWidth(), config.getDisplayHeight()));
      Display.setFullscreen(config.getDisplayFullscreen());
      Display.setVSyncEnabled(config.getDisplayVsync());
      Display.create();

      Defaults.setupGLInvariants();
      Defaults.setupViewportFromDisplay();

      final EventBus eventBus = new EventBus();
      final ClientNetworkManager networkManager = new ClientNetworkManager(eventBus);
      final ClientEntityManager entityManager = new ClientEntityManager();
      eventBus.register(entityManager);
      final ClientTerrainManager terrainManager = new ClientTerrainManager();
      final TerrainRenderer terrainRenderer = new TerrainRenderer();
      final EntityRenderer entityRenderer = new EntityRenderer();
      final Keybinding keybinding = new Keybinding();
      keybinding.bind(Keyboard.KEY_W, PlayerInput.Action.MovingNorth);
      keybinding.bind(Keyboard.KEY_S, PlayerInput.Action.MovingSouth);
      keybinding.bind(Keyboard.KEY_A, PlayerInput.Action.MovingWest);
      keybinding.bind(Keyboard.KEY_D, PlayerInput.Action.MovingEast);
      keybinding.bind(Keyboard.KEY_LSHIFT, PlayerInput.Action.Running);
      keybinding.bind(Keybinding.MOUSE1, PlayerInput.Action.Attacking);

      final List<Activity> activityStack = new LinkedList<>();

      final Object eventHandler = new Object() {

        @Subscribe
        public void onEnterGameEvent(final EnterGameEvent event) throws IOException {
          final Activity activity = new MultiplayerActivity(
            event.clientId,
            event.entityId,
            networkManager,
            entityManager,
            terrainManager,
            terrainRenderer,
            entityRenderer,
            keybinding);
          eventBus.register(activity);
          activityStack.add(0, activity);
        }

        @Subscribe
        public void onConnectedEvent(final ConnectedEvent event) {
          final ByteBuffer packet = Channel.createPacketBuffer();
          Protocol.writePacketHeader(packet, Protocol.PacketType.Hello);
          packet.flip();
          networkManager.sendPacketToServer(packet);
        }

        @Subscribe
        public void onDisconnectedEvent(final DisconnectedEvent event) {
          eventBus.unregister(activityStack.remove(0));
        }
      };
      eventBus.register(eventHandler);

      networkManager.connect("192.168.1.76", 12345);

      int mouseWheel = 0;

      final FpsCounter fpsCounter = new FpsCounter();
      while (!Display.isCloseRequested()) {
        if (!activityStack.isEmpty()) {
          final Activity activity = activityStack.get(0);
          for (int i = 0; i < Keyboard.getNumKeyboardEvents(); ++i) {
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
                Keybinding.MOUSE_WHEELDOWN : Keybinding.MOUSE_WHEELUP;
              if (Mouse.getEventDWheel() == 0) {
                activity.key(key, '\0', true);
              } else {
                activity.key(key, '\0', false);
              }
              mouseWheel = Mouse.getEventDWheel();
            }
          }
          if (!activity.update()) {
            break;
          }
          activity.draw();
        }
        Display.update();
        if (config.getDisplayFps().isPresent()) {
          Display.sync(config.getDisplayFps().get());
        }
        if (fpsCounter.update()) {
          Display.setTitle(String.format("%s (%d FPS)", TITLE, fpsCounter.getFps()));
        }
      }
    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final LWJGLException e) {
      e.printStackTrace();
      System.exit(0);
    } finally {
      Display.destroy();
    }
  }
}
