package mtg.effects.filters.base

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.objects.Card
import mtg.game.state.GameState
import mtg.text.{NounPhraseTemplate, Nouns}

object CardFilter extends Filter[ObjectId] {
  override def matches(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    gameState.gameObjectState.allObjects.find(_.objectId == objectId).exists(_.underlyingObject.isInstanceOf[Card])
  }

  override def getNounPhraseTemplate(cardName: String): NounPhraseTemplate = Nouns.Card

  override def getAll(gameState: GameState, effectContext: EffectContext): Set[ObjectId] = {
    gameState.gameObjectState.allObjects
      .filter(_.underlyingObject.isInstanceOf[Card])
      .map(_.objectId)
      .toSet
  }
}
