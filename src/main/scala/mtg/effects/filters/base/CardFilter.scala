package mtg.effects.filters.base

import mtg.effects.filters.Filter
import mtg.game.ObjectId
import mtg.game.state.GameState

object CardFilter extends Filter[ObjectId] {
  override def isValid(objectId: ObjectId, gameState: GameState): Boolean = {
    // TODO: handle not everything being a card
    true
  }

  override def getText(cardName: String): String = "card"
}
