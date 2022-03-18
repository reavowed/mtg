package mtg.instructions.nouns

import mtg.effects.EffectContext
import mtg.game.state.GameState

trait IndefiniteNounPhrase[T] {
  def getText(cardName: String): String
  def describes(t: T, gameState: GameState, effectContext: EffectContext): Boolean
}
