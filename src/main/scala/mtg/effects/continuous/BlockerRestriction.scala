package mtg.effects.continuous

import mtg.effects.ContinuousObjectEffect
import mtg.game.state.ObjectWithState

trait BlockerRestriction extends ContinuousObjectEffect {
  def preventsBlock(attackerState: ObjectWithState, blockerState: ObjectWithState): Boolean
}
