package mtg.effects.condition.event

import mtg.core.PlayerId
import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.effects.identifiers.StaticIdentifier
import mtg.game.state.{GameState, GameUpdate}
import mtg.game.turns.TurnStep
import mtg.game.turns.turnEvents.ExecuteStep

case class BeginningOfCombatCondition(playerIdentifier: StaticIdentifier[PlayerId]) extends Condition {
  override def getText(cardName: String): String = {
    "the beginning of combat on " +
      playerIdentifier.getPossessiveText(cardName) +
      " turn"
  }
  override def matchesEvent(eventToMatch: GameUpdate, gameState: GameState, effectContext: EffectContext): Boolean = {
    eventToMatch == ExecuteStep(TurnStep.BeginningOfCombatStep) && gameState.activePlayer == playerIdentifier.get(gameState, effectContext)
  }
}
