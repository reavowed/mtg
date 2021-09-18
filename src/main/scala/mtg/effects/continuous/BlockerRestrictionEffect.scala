package mtg.effects.continuous

import mtg.effects.ContinuousEffect
import mtg.game.state.ObjectWithState

trait BlockerRestrictionEffect extends ContinuousEffect {
  def preventsBlock(attackerState: ObjectWithState, blockerState: ObjectWithState): Boolean
}
