package mtg.effects.condition.event

import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.state.{GameAction, GameState}
import mtg.game.turns.turnBasedActions.UntilEndOfTurnEffectsEnd

object EndOfTurnCondition extends Condition {
  override def getText(cardName: String): String = "end of turn"
  override def matchesEvent(eventToMatch: GameAction[_], gameState: GameState, effectContext: EffectContext): Boolean = {
    eventToMatch == UntilEndOfTurnEffectsEnd
  }
}
