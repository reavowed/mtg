package mtg.effects.condition.event

import mtg.effects.EffectContext
import mtg.effects.condition.{Condition, ConditionDefinition, EventCondition}
import mtg.effects.identifiers.StaticIdentifier
import mtg.game.PlayerId
import mtg.game.state.{AutomaticGameAction, GameState}
import mtg.game.turns.TurnStep
import mtg.game.turns.turnEvents.BeginStepEvent

case class BeginningOfCombatConditionDefinition(playerIdentifier: StaticIdentifier[PlayerId]) extends ConditionDefinition {
  override def getText(cardName: String): String = "the beginning of combat on " + playerIdentifier.getPossessiveText(cardName) + " turn"
  override def getCondition(gameState: GameState, effectContext: EffectContext): Condition = BeginningOfCombatCondition(playerIdentifier.get(effectContext, gameState))
}

case class BeginningOfCombatCondition(player: PlayerId) extends EventCondition {
  override def matchesEvent(eventToMatch: AutomaticGameAction, gameState: GameState): Boolean = {
    eventToMatch == BeginStepEvent(TurnStep.BeginningOfCombatStep) && gameState.activePlayer == player
  }
}
