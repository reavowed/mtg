package mtg.instructions.nouns

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.game.objects.{Card, CopyOfSpell, StackObject}
import mtg.game.state.{CurrentCharacteristics, GameState}

case object Spell extends Noun.RegularCaseObject[ObjectId] {
  override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    CurrentCharacteristics.getStackObject(objectId, gameState).exists(isSpell)
  }
  private def isSpell(stackObject: StackObject): Boolean = {
    stackObject.underlyingObject.isInstanceOf[Card] || stackObject.underlyingObject.isInstanceOf[CopyOfSpell]
  }
}
