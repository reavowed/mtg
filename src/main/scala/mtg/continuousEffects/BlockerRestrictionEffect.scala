package mtg.continuousEffects

import mtg.game.state.ObjectWithState

trait BlockerRestrictionEffect extends ContinuousEffect {
  def preventsBlock(attackerState: ObjectWithState, blockerState: ObjectWithState): Boolean
}
