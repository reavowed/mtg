package mtg.effects.filters.base

import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.ObjectId
import mtg.game.state.GameState

object PermanentFilter extends Filter[ObjectId] {
  override def matches(objectId: ObjectId, effectContext: EffectContext, gameState: GameState): Boolean = {
    objectId.findPermanent(gameState).nonEmpty
  }
  override def getText(cardName: String): String = "permanent"

  override def getAll(effectContext: EffectContext, gameState: GameState): Set[ObjectId] = gameState.gameObjectState.battlefield.map(_.objectId).toSet
}
