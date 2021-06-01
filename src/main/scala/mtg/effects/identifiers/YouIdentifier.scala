package mtg.effects.identifiers

import mtg.effects.oneshot.OneShotEffectResolutionContext
import mtg.game.{ObjectId, PlayerId}
import mtg.game.state.GameState

object YouIdentifier extends Identifier[PlayerId] {
  override def get(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): (PlayerId, OneShotEffectResolutionContext) = {
    (resolutionContext.controller, resolutionContext)
  }
  override def getText(cardName: String): String = "you"
  override def getPossessiveText(cardName: String): String = "your"
}
