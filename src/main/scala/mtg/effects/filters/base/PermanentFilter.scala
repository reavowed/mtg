package mtg.effects.filters.base

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.state.{CurrentCharacteristics, GameState}
import mtg.text.{NounPhraseTemplate, Nouns}

object PermanentFilter extends Filter[ObjectId] {
  override def getSingular(cardName: String): String = "permanent"
  override def getAll(gameState: GameState, effectContext: EffectContext): Seq[ObjectId] = {
    gameState.gameObjectState.battlefield.map(_.objectId)
  }
}
