package mtg.continuousEffects

import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.state.{GameState, InternalGameAction}

case class FloatingActiveContinuousEffect(effect: ContinuousEffect, context: EffectContext, endCondition: Condition) {
  def matchesEndCondition(action: InternalGameAction, gameStateAfterAction: GameState): Boolean = {
    endCondition.matchesEvent(action, gameStateAfterAction, context)
  }
}
