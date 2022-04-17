package mtg.instructions.nounPhrases

import mtg.core.PlayerId
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.VerbPerson

object You extends IndefiniteNounPhrase[PlayerId] with StaticSingleIdentifyingNounPhrase[PlayerId] {
  override def getText(cardName: String): String = "you"

  override def getPossessiveText(cardName: String): String = "your"

  override def person: VerbPerson = VerbPerson.Second

  override def describes(playerId: PlayerId, gameState: GameState, effectContext: EffectContext): Boolean = {
    playerId == effectContext.youPlayerId
  }

  override def identify(gameState: GameState, effectContext: EffectContext): PlayerId = {
    effectContext.youPlayerId
  }
}
