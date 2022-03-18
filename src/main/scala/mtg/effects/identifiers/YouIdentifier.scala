package mtg.effects.identifiers

import mtg.core.PlayerId
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.text.{NounPhrase, NounPhrases, VerbPerson}

object YouIdentifier extends StaticIdentifier[PlayerId] {
  override def get(gameState: GameState, effectContext: EffectContext): PlayerId = effectContext.controllingPlayer
  override def getNounPhrase(cardName: String): NounPhrase = NounPhrases.You
  override def person: VerbPerson = VerbPerson.Second
}
