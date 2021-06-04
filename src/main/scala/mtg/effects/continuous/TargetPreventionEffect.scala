package mtg.effects.continuous

import mtg.effects.ContinuousEffect
import mtg.game.ObjectOrPlayer
import mtg.game.state.{GameState, StackObjectWithState}

trait TargetPreventionEffect extends ContinuousEffect {
  def preventsTarget(source: StackObjectWithState, target: ObjectOrPlayer, gameState: GameState): Boolean
}
