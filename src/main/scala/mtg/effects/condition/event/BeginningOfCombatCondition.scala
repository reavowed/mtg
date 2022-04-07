package mtg.effects.condition.event

import mtg.core.PlayerId
import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.state.history.HistoryEvent
import mtg.game.state.{GameAction, GameState}
import mtg.game.turns.TurnStep
import mtg.game.turns.turnEvents.{BeginStep, ExecuteStep}
import mtg.instructions.nounPhrases.StaticSingleIdentifyingNounPhrase

case class BeginningOfCombatCondition(playerPhrase: StaticSingleIdentifyingNounPhrase[PlayerId]) extends Condition {
  override def getText(cardName: String): String = {
    "the beginning of combat on " +
      playerPhrase.getPossessiveText(cardName) +
      " turn"
  }
  override def matchesEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, effectContext: EffectContext): Boolean = {
    eventToMatch.action == BeginStep(TurnStep.BeginningOfCombatStep) && gameState.activePlayer == playerPhrase.identify(gameState, effectContext)
  }
}
