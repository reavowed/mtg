package mtg.continuousEffects

import mtg.core.ObjectOrPlayerId
import mtg.game.state.{GameState, StackObjectWithState}

trait TargetPreventionEffect extends ContinuousEffect {
  def preventsTarget(source: StackObjectWithState, target: ObjectOrPlayerId, gameState: GameState): Boolean
}
