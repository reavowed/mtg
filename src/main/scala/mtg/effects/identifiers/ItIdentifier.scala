package mtg.effects.identifiers

import mtg.effects.oneshot.OneShotEffectResolutionContext
import mtg.game.ObjectId
import mtg.game.state.GameState

object ItIdentifier extends Identifier[ObjectId] {
  override def get(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): (ObjectId, OneShotEffectResolutionContext) = {
    (resolutionContext.identifiedObjects.last, resolutionContext)
  }
  override def getText(cardName: String): String = "it"
}
