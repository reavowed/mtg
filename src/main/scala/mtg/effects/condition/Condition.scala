package mtg.effects.condition

import mtg.effects.EffectContext
import mtg.game.state.{GameState, GameUpdate}
import mtg.instructions.TextComponent

trait Condition extends TextComponent {
  def matchesEvent(eventToMatch: GameUpdate, gameState: GameState, effectContext: EffectContext): Boolean
}
