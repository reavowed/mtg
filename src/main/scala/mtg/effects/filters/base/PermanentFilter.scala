package mtg.effects.filters.base

import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.ObjectId
import mtg.game.state.GameState
import mtg.text.{NounPhraseTemplate, Nouns}

object PermanentFilter extends Filter[ObjectId] {
  override def matches(objectId: ObjectId, effectContext: EffectContext, gameState: GameState): Boolean = {
    objectId.findPermanent(gameState).nonEmpty
  }

  override def getNounPhraseTemplate(cardName: String): NounPhraseTemplate = Nouns.Permanent

  override def getAll(effectContext: EffectContext, gameState: GameState): Set[ObjectId] = gameState.gameObjectState.battlefield.map(_.objectId).toSet
}
