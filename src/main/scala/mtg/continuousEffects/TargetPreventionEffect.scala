package mtg.continuousEffects

import mtg.definitions.ObjectOrPlayerId
import mtg.game.state.{GameState, StackObjectWithState}

trait TargetPreventionEffect extends ContinuousEffect {
  def preventsTarget(source: StackObjectWithState, target: ObjectOrPlayerId, gameState: GameState): Boolean
}
