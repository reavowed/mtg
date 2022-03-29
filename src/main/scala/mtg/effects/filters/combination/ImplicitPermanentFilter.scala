package mtg.effects.filters.combination

import mtg.core.ObjectId
import mtg.core.types.Type
import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.effects.filters.base.{PermanentFilter, TypeFilter}
import mtg.game.state.GameState
import mtg.text.NounPhraseTemplate

class ImplicitPermanentFilter(t: Type) extends Filter[ObjectId] {
  override def getSingular(cardName: String): String = t.name.toLowerCase
  override def getPlural(cardName: String): String = if (t == Type.Sorcery) "sorceries" else super.getPlural(cardName)
  override def matches(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    PermanentFilter.matches(objectId, gameState, effectContext) && TypeFilter(t).matches(objectId, gameState, effectContext)
  }
  override def getAll(gameState: GameState, effectContext: EffectContext): Set[ObjectId] = PermanentFilter.getAll(gameState, effectContext)
    .filter(gameState.gameObjectState.getCurrentOrLastKnownState(_).characteristics.types.contains(t))
}
