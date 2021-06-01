package mtg.effects.identifiers

import mtg.effects.oneshot.OneShotEffectResolutionContext
import mtg.game.ObjectId
import mtg.game.state.GameState

object CardNameIdentifier extends Identifier[ObjectId] {
  override def get(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): (ObjectId, OneShotEffectResolutionContext) = {
    (resolutionContext.resolvingObject, resolutionContext.addIdentifiedObject(resolutionContext.resolvingObject))
  }
  override def getText(cardName: String): String = cardName
}