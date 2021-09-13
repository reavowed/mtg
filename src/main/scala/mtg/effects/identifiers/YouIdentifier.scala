package mtg.effects.identifiers

import mtg.effects.EffectContext
import mtg.game.PlayerId
import mtg.game.state.GameState

object YouIdentifier extends StaticIdentifier[PlayerId] {
  override def get(effectContext: EffectContext, gameState: GameState): PlayerId = effectContext.controllingPlayer

  override def getText(cardName: String): String = "you"
  override def getPossessiveText(cardName: String): String = "your"
}
