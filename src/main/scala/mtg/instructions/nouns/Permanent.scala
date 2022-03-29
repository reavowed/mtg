package mtg.instructions.nouns

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.game.objects.{Card, CopyOfSpell, StackObject}
import mtg.game.state.GameState

case object Permanent extends Noun.RegularCaseObject[ObjectId] {
  override def getAll(gameState: GameState, effectContext: EffectContext): Seq[ObjectId] = {
    gameState.gameObjectState.battlefield.map(_.objectId)
  }
}
