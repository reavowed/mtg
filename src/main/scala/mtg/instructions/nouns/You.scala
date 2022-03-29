package mtg.instructions.nouns

import mtg.core.PlayerId
import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.game.state.GameState
import mtg.text.{VerbNumber, VerbPerson}

object You extends IndefiniteNounPhrase[PlayerId] with StaticSingleIdentifyingNounPhrase[PlayerId] {
  override def getText(cardName: String): String = "you"
  override def getPossessiveText(cardName: String): String = "your"
  override def person: VerbPerson = VerbPerson.Second
  override def describes(playerId: PlayerId, gameState: GameState, effectContext: EffectContext): Boolean = {
    playerId == effectContext.controllingPlayer
  }
  override def identify(gameState: GameState, effectContext: EffectContext): PlayerId = {
    effectContext.controllingPlayer
  }
}
