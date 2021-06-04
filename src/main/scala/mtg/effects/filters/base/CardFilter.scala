package mtg.effects.filters.base

import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.ObjectId
import mtg.game.state.GameState

object CardFilter extends Filter[ObjectId] {
  override def isValid(objectId: ObjectId, effectContext: EffectContext, gameState: GameState): Boolean = {
    // TODO: handle not everything being a card
    true
  }

  override def getText(cardName: String): String = "card"
}
