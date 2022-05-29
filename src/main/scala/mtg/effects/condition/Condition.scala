package mtg.effects.condition

import mtg.effects.{EffectContext, InstructionResolutionContext}
import mtg.game.state.history.HistoryEvent
import mtg.game.state.{GameAction, GameState}
import mtg.instructions.TextComponent

trait Condition extends TextComponent {
  def looksBackInTime: Boolean = false
  def matchEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, context: InstructionResolutionContext): Option[InstructionResolutionContext]
}

object Condition {
  trait Simple extends Condition {
    def matchesEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, context: EffectContext): Boolean
    override def matchEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, context: InstructionResolutionContext): Option[InstructionResolutionContext] = {
      if (matchesEvent(eventToMatch, gameState, context)) Some(context) else None
    }
  }
}
