package mtg.effects.continuous

import mtg.core.ObjectOrPlayerId
import mtg.effects.ContinuousEffect
import mtg.game.state.{GameState, StackObjectWithState}

trait TargetPreventionEffect extends ContinuousEffect {
  def preventsTarget(source: StackObjectWithState, target: ObjectOrPlayerId, gameState: GameState): Boolean
}
