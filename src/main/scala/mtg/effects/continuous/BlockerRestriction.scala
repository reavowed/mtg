package mtg.effects.continuous

import mtg.effects.ContinuousEffect
import mtg.game.state.ObjectWithState

trait BlockerRestriction extends ContinuousEffect {
  def preventsBlock(attackerState: ObjectWithState, blockerState: ObjectWithState): Boolean
}
