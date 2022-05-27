package mtg.instructions.nouns

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.game.objects.{Card, CopyOfSpell, StackObject}
import mtg.game.state.GameState

case object Spell extends ClassNoun[ObjectId] with Noun.RegularCaseObject  {
  override def getAll(gameState: GameState, effectContext: EffectContext): Seq[ObjectId] = {
    gameState.gameObjectState.stack.filter(isSpell).map(_.objectId)
  }
  private def isSpell(stackObject: StackObject): Boolean = {
    stackObject.underlyingObject.isInstanceOf[Card] || stackObject.underlyingObject.isInstanceOf[CopyOfSpell]
  }
}
