package mtg.instructions.nouns

import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.TextComponent

trait IndefiniteNounPhrase[T] extends TextComponent {
  def describes(t: T, gameState: GameState, effectContext: EffectContext): Boolean
}
