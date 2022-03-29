package mtg.effects.condition.event

import mtg.core.PlayerId
import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.state.{GameState, GameUpdate}
import mtg.game.turns.TurnStep
import mtg.game.turns.turnEvents.ExecuteStep
import mtg.instructions.nouns.StaticSingleIdentifyingNounPhrase

case class BeginningOfCombatCondition(playerPhrase: StaticSingleIdentifyingNounPhrase[PlayerId]) extends Condition {
  override def getText(cardName: String): String = {
    "the beginning of combat on " +
      playerPhrase.getPossessiveText(cardName) +
      " turn"
  }
  override def matchesEvent(eventToMatch: GameUpdate, gameState: GameState, effectContext: EffectContext): Boolean = {
    eventToMatch == ExecuteStep(TurnStep.BeginningOfCombatStep) && gameState.activePlayer == playerPhrase.identify(gameState, effectContext)
  }
}
