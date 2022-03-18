package mtg.effects.condition.event

import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.state.{GameState, GameUpdate}
import mtg.game.turns.turnBasedActions.UntilEndOfTurnEffectsEnd

object EndOfTurnCondition extends Condition {
  override def getText(cardName: String): String = "end of turn"
  override def matchesEvent(eventToMatch: GameUpdate, gameState: GameState, effectContext: EffectContext): Boolean = {
    eventToMatch == UntilEndOfTurnEffectsEnd
  }
}
