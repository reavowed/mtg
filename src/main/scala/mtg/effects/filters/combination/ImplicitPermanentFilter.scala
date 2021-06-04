package mtg.effects.filters.combination

import mtg.characteristics.types.Type
import mtg.effects.filters.Filter
import mtg.effects.filters.base.{PermanentFilter, TypeFilter}
import mtg.game.ObjectId
import mtg.game.state.GameState

class ImplicitPermanentFilter(t: Type) extends Filter[ObjectId] {
  override def isValid(objectId: ObjectId, gameState: GameState): Boolean = PermanentFilter.matches(objectId, gameState) && TypeFilter(t).matches(objectId, gameState)
  override def text: String = t.name.toLowerCase
}
