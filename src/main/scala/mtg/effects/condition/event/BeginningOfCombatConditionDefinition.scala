package mtg.effects.condition.event

import mtg.core.PlayerId
import mtg.effects.EffectContext
import mtg.effects.condition.{Condition, ConditionDefinition, EventCondition}
import mtg.effects.identifiers.StaticIdentifier
import mtg.game.state.{GameState, GameUpdate}
import mtg.game.turns.TurnStep
import mtg.game.turns.turnEvents.ExecuteStep

case class BeginningOfCombatConditionDefinition(playerIdentifier: StaticIdentifier[PlayerId]) extends ConditionDefinition {
  override def getText(cardName: String): String = {
    "the beginning of combat on " +
      playerIdentifier.getNounPhrase(cardName).possessiveText +
      " turn"
  }
  override def getCondition(gameState: GameState, effectContext: EffectContext): Condition = BeginningOfCombatCondition(playerIdentifier.get(effectContext, gameState))
}

case class BeginningOfCombatCondition(player: PlayerId) extends EventCondition {
  override def matchesEvent(eventToMatch: GameUpdate, gameState: GameState): Boolean = {
    eventToMatch == ExecuteStep(TurnStep.BeginningOfCombatStep) && gameState.activePlayer == player
  }
}
