package mtg.effects.filters.base

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.objects.Card
import mtg.game.state.GameState
import mtg.text.{NounPhraseTemplate, Nouns}

object CardFilter extends Filter[ObjectId] {
  override def getSingular(cardName: String): String = "card"
  override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    gameState.gameObjectState.allObjects.find(_.objectId == objectId).exists(_.underlyingObject.isInstanceOf[Card])
  }
}
