package mtg.effects.continuous

import mtg.effects.ContinuousObjectEffect
import mtg.game.ObjectOrPlayer
import mtg.game.state.{GameState, StackObjectWithState}

trait TargetPreventionEffect extends ContinuousObjectEffect {
  def preventsTarget(source: StackObjectWithState, target: ObjectOrPlayer, gameState: GameState): Boolean
}
