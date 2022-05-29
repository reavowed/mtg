package mtg.continuousEffects

import mtg.effects.InstructionResolutionContext
import mtg.effects.condition.Condition
import mtg.game.state.GameState
import mtg.game.state.history.HistoryEvent

case class FloatingActiveContinuousEffect(effect: ContinuousEffect, context: InstructionResolutionContext, endCondition: Condition) {
  def matchesEndCondition(eventToMatch: HistoryEvent.ResolvedAction[_], gameStateAfterAction: GameState): Boolean = {
    endCondition.matchEvent(eventToMatch, gameStateAfterAction, context).isDefined
  }
}
