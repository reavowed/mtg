package mtg.effects.filters.base

import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.ObjectId
import mtg.game.objects.Card
import mtg.game.state.GameState

object CardFilter extends Filter[ObjectId] {
  override def isValid(objectId: ObjectId, effectContext: EffectContext, gameState: GameState): Boolean = {
    gameState.gameObjectState.allObjects.find(_.objectId == objectId).exists(_.underlyingObject.isInstanceOf[Card])
  }

  override def getText(cardName: String): String = "card"
}
