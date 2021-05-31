package mtg.effects.condition.event

import mtg.effects.condition.{Condition, ConditionDefinition, SingleEventCondition}
import mtg.effects.oneshot.OneShotEffectResolutionContext
import mtg.game.state.GameState
import mtg.game.turns.turnBasedActions.UntilEndOfTurnEffectsEnd

object EndOfTurnConditionDefinition extends ConditionDefinition {
  override def getText(cardName: String): String = "end of turn"
  override def getCondition(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): Condition = SingleEventCondition(UntilEndOfTurnEffectsEnd(gameState.currentTurn.get))
}
