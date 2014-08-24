package org.fryingpanjoe.bigbattle.server.game.ai;

import java.util.Random;

import org.fryingpanjoe.bigbattle.server.Intersecting;
import org.fryingpanjoe.bigbattle.server.game.ServerEntity;
import org.lwjgl.util.vector.Vector2f;

public class Roaming {

  private static final Random RANDOM = new Random();

  private final ServerEntity entity;
  private final float minDist;
  private final float maxDist;
  private final float minThinkTime;
  private final float maxThinkTime;
  private final Vector2f target;
  private float thinkTime;

  public Roaming(final ServerEntity entity,
                 final float minMoveDistance,
                 final float maxMoveDistance,
                 final float minThinkTime,
                 final float maxThinkTime) {
    this.entity = entity;
    this.minDist = minMoveDistance;
    this.maxDist = maxMoveDistance;
    this.minThinkTime = minThinkTime;
    this.maxThinkTime = maxThinkTime;
    this.target = new Vector2f();
    this.thinkTime = 0.f;
  }

  public void think(final float dt) {
    if (isTargetReached()) {
      if (this.thinkTime <= 0.f) {
        this.thinkTime = randomThinkTime();
      } else {
        this.thinkTime -= dt;
        if (this.thinkTime <= 0.f) {
          final float direction = randomDirection();
          final float radius = randomDistance();
          this.target.x = this.entity.getEntity().getX() + radius * (float) Math.cos(direction);
          this.target.y = this.entity.getEntity().getY() + radius * (float) Math.sin(direction);
        }
      }
    } else {
      final Vector2f moveDir = new Vector2f(
        this.target.x - this.entity.getEntity().getX(),
        this.target.y - this.entity.getEntity().getY());
      moveDir.normalise();
      final Vector2f velocity = new Vector2f(
        this.entity.getEntity().getVelocityX(),
        this.entity.getEntity().getVelocityY());
      final float turnSpeedFactor = Math.abs(Vector2f.angle(velocity, moveDir)) / (float) Math.PI;
      final float speed = this.entity.getEntity().getDef().getSpeed();
      velocity.x += dt * turnSpeedFactor * speed * moveDir.x + velocity.x;
      velocity.y += dt * turnSpeedFactor * speed * moveDir.y + velocity.y;
      velocity.scale(speed / (float) velocity.length());
      this.entity.getEntity().setVelocity(velocity.x, velocity.y);
    }
  }

  private float randomDirection() {
    final float halfPi = (float) Math.PI * 0.5f;
    return this.entity.getEntity().getRotation() + random(-halfPi, halfPi);
  }

  private float randomDistance() {
    return random(this.minDist, this.maxDist);
  }

  private float randomThinkTime() {
    return random(this.minThinkTime, this.maxThinkTime);
  }

  private static float random(final float min, final float max) {
    return min + RANDOM.nextFloat() * (max - min);
  }

  private boolean isTargetReached() {
    return Intersecting.pointSphere(
      this.target.x,
      this.target.y,
      this.entity.getEntity().getX(),
      this.entity.getEntity().getY(),
      this.entity.getEntity().getDef().getRadius());
  }
}
