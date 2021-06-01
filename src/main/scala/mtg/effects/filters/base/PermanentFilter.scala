package mtg.effects.filters.base

import mtg.effects.filters.Filter
import mtg.game.ObjectId
import mtg.game.state.GameState

object PermanentFilter extends Filter[ObjectId] {
  override def isValid(objectId: ObjectId, gameState: GameState): Boolean = {
    objectId.findPermanent(gameState).nonEmpty
  }
  override def text: String = "permanent"
}
