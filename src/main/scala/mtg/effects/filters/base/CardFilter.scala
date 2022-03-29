package mtg.effects.filters.base

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.objects.Card
import mtg.game.state.GameState
import mtg.text.{NounPhraseTemplate, Nouns}

object CardFilter extends Filter[ObjectId] {
  override def getSingular(cardName: String): String = "card"
  override def getAll(gameState: GameState, effectContext: EffectContext): Seq[ObjectId] = {
    gameState.gameObjectState.allObjects.filter(_.underlyingObject.isInstanceOf[Card]).map(_.objectId).toSeq
  }
}
