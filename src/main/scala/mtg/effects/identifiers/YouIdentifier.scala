package mtg.effects.identifiers

import mtg.effects.EffectContext
import mtg.game.PlayerId
import mtg.game.state.GameState
import mtg.text.{NounPhrase, NounPhrases}

object YouIdentifier extends StaticIdentifier[PlayerId] {
  override def get(effectContext: EffectContext, gameState: GameState): PlayerId = effectContext.controllingPlayer
  override def getNounPhrase(cardName: String): NounPhrase = NounPhrases.You
}
