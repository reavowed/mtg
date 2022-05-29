package mtg.effects.condition.event

import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.state.GameState
import mtg.game.state.history.HistoryEvent
import mtg.game.turns.turnBasedActions.EndOfTurnEffectsEnd

object EndOfTurnCondition extends Condition.Simple {
  override def getText(cardName: String): String = "end of turn"
  override def matchesEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, effectContext: EffectContext): Boolean = {
    eventToMatch.action == EndOfTurnEffectsEnd
  }
}
