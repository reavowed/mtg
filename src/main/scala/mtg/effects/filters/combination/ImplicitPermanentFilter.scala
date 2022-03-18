package mtg.effects.filters.combination

import mtg.core.ObjectId
import mtg.core.types.Type
import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.effects.filters.base.{PermanentFilter, TypeFilter}
import mtg.game.state.GameState
import mtg.text.NounPhraseTemplate

class ImplicitPermanentFilter(t: Type) extends Filter[ObjectId] {
  override def matches(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    PermanentFilter.matches(objectId, gameState, effectContext) && TypeFilter(t).matches(objectId, gameState, effectContext)
  }

  override def getNounPhraseTemplate(cardName: String): NounPhraseTemplate = t match {
    case Type.Sorcery =>
      NounPhraseTemplate.Simple("sorcery", "sorceries")
    case otherType =>
      NounPhraseTemplate.Simple(otherType.name.toLowerCase)
  }

  override def getAll(gameState: GameState, effectContext: EffectContext): Set[ObjectId] = PermanentFilter.getAll(gameState, effectContext)
    .filter(gameState.gameObjectState.getCurrentOrLastKnownState(_).characteristics.types.contains(t))
}
