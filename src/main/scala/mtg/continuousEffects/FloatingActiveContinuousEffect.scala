package mtg.continuousEffects

import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.state.history.HistoryEvent
import mtg.game.state.{GameAction, GameState}

case class FloatingActiveContinuousEffect(effect: ContinuousEffect, context: EffectContext, endCondition: Condition) {
  def matchesEndCondition(eventToMatch: HistoryEvent.ResolvedAction[_], gameStateAfterAction: GameState): Boolean = {
    endCondition.matchesEvent(eventToMatch, gameStateAfterAction, context)
  }
}
