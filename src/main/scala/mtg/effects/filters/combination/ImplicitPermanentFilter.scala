package mtg.effects.filters.combination

import mtg.characteristics.types.Type
import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.effects.filters.base.{PermanentFilter, TypeFilter}
import mtg.game.ObjectId
import mtg.game.state.GameState

class ImplicitPermanentFilter(t: Type) extends Filter[ObjectId] {
  override def isValid(objectId: ObjectId, effectContext: EffectContext, gameState: GameState): Boolean = {
    PermanentFilter.matches(objectId, effectContext, gameState) && TypeFilter(t).matches(objectId, effectContext, gameState)
  }
  override def getText(cardName: String): String = t.name.toLowerCase
}
