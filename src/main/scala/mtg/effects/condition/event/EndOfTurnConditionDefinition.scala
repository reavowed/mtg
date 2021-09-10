package mtg.effects.condition.event

import mtg.effects.EffectContext
import mtg.effects.condition.{Condition, ConditionDefinition, SingleEventCondition}
import mtg.game.state.GameState
import mtg.game.turns.turnBasedActions.UntilEndOfTurnEffectsEnd

object EndOfTurnConditionDefinition extends ConditionDefinition {
  override def getText(cardName: String): String = "end of turn"
  override def getCondition(gameState: GameState, effectContext: EffectContext): Condition = SingleEventCondition(UntilEndOfTurnEffectsEnd(gameState.turnState.currentTurn.get))
}
