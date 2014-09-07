package org.fryingpanjoe.bigbattle.common.game;

public class PlayerInputController {

  private PlayerInputController() {
  }

  public static void respondToPlayerInput(final Entity entity, final PlayerInput playerInput) {
    float speedFactor = 1.f;
    float velx = 0.f;
    float vely = 0.f;
    for (final PlayerInput.Action action : playerInput.getActions()) {
      switch (action) {
        case Running:
          speedFactor = 2.f;
          break;

        case MovingNorth:
          velx += -1.0f;
          vely += -1.0f;
          break;

        case MovingSouth:
          velx += 1.0f;
          vely += 1.0f;
          break;

        case MovingWest:
          velx += -1.0f;
          vely += 1.0f;
          break;

        case MovingEast:
          velx += 1.0f;
          vely += -1.0f;
          break;

        case Attacking:
          // TODO implement
          break;
      }
    }
    if (velx != 0.f || vely != 0.f) {
      final float moveLength = (float) Math.sqrt((velx * velx) + (vely * vely));
      final float baseSpeed = entity.getDefinition().getSpeed();
      velx *= speedFactor * baseSpeed / moveLength;
      vely *= speedFactor * baseSpeed / moveLength;
      entity.setVelocity(velx, vely);
    } else {
      entity.setVelocity(0.f, 0.f);
    }
    if (entity.getRotation() != playerInput.getRotation()) {
      entity.setRotation(playerInput.getRotation());
    }
  }
}
