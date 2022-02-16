package mtg.effects.filters.combination

import mtg.characteristics.types.Type
import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.effects.filters.base.{PermanentFilter, TypeFilter}
import mtg.game.state.GameState
import mtg.text.NounPhraseTemplate

class ImplicitPermanentFilter(t: Type) extends Filter[ObjectId] {
  override def matches(objectId: ObjectId, effectContext: EffectContext, gameState: GameState): Boolean = {
    PermanentFilter.matches(objectId, effectContext, gameState) && TypeFilter(t).matches(objectId, effectContext, gameState)
  }

  override def getNounPhraseTemplate(cardName: String): NounPhraseTemplate = t.nounPhraseTemplate

  override def getAll(effectContext: EffectContext, gameState: GameState): Set[ObjectId] = PermanentFilter.getAll(effectContext, gameState)
    .filter(gameState.gameObjectState.getCurrentOrLastKnownState(_).characteristics.types.contains(t))
}
