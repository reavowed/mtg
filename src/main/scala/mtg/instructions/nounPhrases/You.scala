package mtg.instructions.nounPhrases

import mtg.definitions.PlayerId
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.grammar.GrammaticalPerson

object You extends IndefiniteNounPhrase[PlayerId] with StaticSingleIdentifyingNounPhrase[PlayerId] {
  override def getText(cardName: String): String = "you"

  override def getPossessiveText(cardName: String): String = "your"

  override def person: GrammaticalPerson = GrammaticalPerson.Second

  override def describes(playerId: PlayerId, gameState: GameState, effectContext: EffectContext): Boolean = {
    playerId == effectContext.youPlayerId
  }

  override def identify(effectContext: EffectContext): PlayerId = {
    effectContext.youPlayerId
  }
}
