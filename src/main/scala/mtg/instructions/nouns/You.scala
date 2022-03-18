package mtg.instructions.nouns

import mtg.core.PlayerId
import mtg.effects.EffectContext
import mtg.game.state.GameState

object You extends IndefiniteNounPhrase[PlayerId] {
  override def getText(cardName: String): String = "you"

  override def describes(playerId: PlayerId, gameState: GameState, effectContext: EffectContext): Boolean = {
    playerId == effectContext.controllingPlayer
  }
}
