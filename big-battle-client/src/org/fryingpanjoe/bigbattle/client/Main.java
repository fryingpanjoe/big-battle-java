package org.fryingpanjoe.bigbattle.client;

import java.io.IOException;
import java.util.logging.Logger;

import org.fryingpanjoe.bigbattle.client.activities.Activity;
import org.fryingpanjoe.bigbattle.client.activities.MultiplayerActivity;
import org.fryingpanjoe.bigbattle.client.config.ClientConfig;
import org.fryingpanjoe.bigbattle.client.rendering.Defaults;
import org.fryingpanjoe.bigbattle.client.rendering.EntityRenderer;
import org.fryingpanjoe.bigbattle.client.rendering.TerrainRenderer;
import org.fryingpanjoe.bigbattle.common.game.PlayerInput;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.google.common.eventbus.EventBus;

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
      final ClientNetworkManager clientNetworkManager = new ClientNetworkManager(eventBus);
      final ClientEntityManager clientEntityManager = new ClientEntityManager();
      eventBus.register(clientEntityManager);
      final ClientPlayerManager clientPlayerManager = new ClientPlayerManager(clientEntityManager);
      eventBus.register(clientPlayerManager);
      final ClientTerrainManager clientTerrainManager = new ClientTerrainManager();
      final TerrainRenderer terrainRenderer = new TerrainRenderer();
      final EntityRenderer entityRenderer = new EntityRenderer();
      final Keybinding keybinding = new Keybinding();
      keybinding.bind(Keyboard.KEY_W, PlayerInput.Action.MovingForward);
      keybinding.bind(Keyboard.KEY_S, PlayerInput.Action.MovingBackward);
      keybinding.bind(Keyboard.KEY_A, PlayerInput.Action.MovingLeft);
      keybinding.bind(Keyboard.KEY_D, PlayerInput.Action.MovingRight);
      keybinding.bind(Keyboard.KEY_LSHIFT, PlayerInput.Action.Running);
      keybinding.bind(Keybinding.MOUSE1, PlayerInput.Action.Attacking);
      keybinding.bind(Keybinding.MOUSE2, PlayerInput.Action.UsingItem);
      final Activity activity = new MultiplayerActivity(
        eventBus,
        clientNetworkManager,
        clientEntityManager,
        clientPlayerManager,
        clientTerrainManager,
        terrainRenderer,
        entityRenderer,
        keybinding);
      eventBus.register(activity);

      clientNetworkManager.connect("192.168.1.76", 12345);

      int mouseWheel = 0;

      final FpsCounter fpsCounter = new FpsCounter();
      while (!Display.isCloseRequested()) {
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
