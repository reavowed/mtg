package mtg.continuousEffects

import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.state.{GameState, GameObjectAction}

case class FloatingActiveContinuousEffect(effect: ContinuousEffect, context: EffectContext, endCondition: Condition) {
  def matchesEndCondition(action: GameObjectAction, gameStateAfterAction: GameState): Boolean = {
    endCondition.matchesEvent(action, gameStateAfterAction, context)
  }
}
