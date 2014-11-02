package org.fryingpanjoe.bigbattle.common.game.ai.behaviors;

import java.util.List;

import com.google.common.collect.Lists;

public class Sequenced implements Behavior {

  private final List<Behavior> behaviors;

  public Sequenced(final Iterable<Behavior> behaviors) {
    this.behaviors = Lists.newArrayList(behaviors);
  }

  public Sequenced(final Behavior...behaviors) {
    this.behaviors = Lists.newArrayList(behaviors);
  }

  @Override
  public boolean apply(final float dt) {
    if (!this.behaviors.isEmpty()) {
      if (!this.behaviors.get(0).apply(dt)) {
        this.behaviors.remove(0);
      }
    }
    return !this.behaviors.isEmpty();
  }
}
