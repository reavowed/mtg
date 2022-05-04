package mtg.effects.condition

import mtg.effects.EffectContext
import mtg.game.state.history.HistoryEvent
import mtg.game.state.{GameAction, GameState}
import mtg.instructions.TextComponent

trait Condition extends TextComponent {
  def looksBackInTime: Boolean = false
  def matchesEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, effectContext: EffectContext): Boolean
}
