package mtg.effects.condition

import mtg.effects.EffectContext
import mtg.game.state.{GameAction, GameState}
import mtg.instructions.TextComponent

trait Condition extends TextComponent {
  def matchesEvent(eventToMatch: GameAction[_], gameState: GameState, effectContext: EffectContext): Boolean
}
