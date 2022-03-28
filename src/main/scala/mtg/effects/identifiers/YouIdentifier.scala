package mtg.effects.identifiers

import mtg.core.PlayerId
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.text.VerbPerson

object YouIdentifier extends StaticIdentifier[PlayerId] {
  override def getText(cardName: String): String = "you"
  override def getPossessiveText(cardName: String): String = "your"
  override def person: VerbPerson = VerbPerson.Second
  override def get(gameState: GameState, effectContext: EffectContext): PlayerId = effectContext.controllingPlayer
}
