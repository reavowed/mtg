package mtg.effects.filters.combination

import mtg.core.ObjectId
import mtg.core.types.Type
import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.effects.filters.base.{PermanentFilter, TypeFilter}
import mtg.game.state.GameState

class ImplicitPermanentFilter(t: Type) extends Filter[ObjectId] {
  override def getSingular(cardName: String): String = t.name.toLowerCase
  override def getPlural(cardName: String): String = if (t == Type.Sorcery) "sorceries" else super.getPlural(cardName)
  override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    PermanentFilter.describes(objectId, gameState, effectContext) && TypeFilter(t).matches(objectId, gameState, effectContext)
  }
}
