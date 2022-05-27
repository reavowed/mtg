package mtg.instructions.nouns

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.game.state.GameState

object Card extends ClassNoun[ObjectId] with Noun.RegularCaseObject {
  override def getAll(gameState: GameState, effectContext: EffectContext): Seq[ObjectId] = {
    gameState.gameObjectState.allObjects.filter(_.underlyingObject.isInstanceOf[mtg.game.objects.Card]).map(_.objectId).toSeq
  }
}
