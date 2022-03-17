package mtg.effects.filters.base

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.objects.{Card, CopyOfSpell, StackObject}
import mtg.game.state.{CurrentCharacteristics, GameState}
import mtg.text.{NounPhraseTemplate, Nouns}

object SpellFilter extends Filter[ObjectId] {
  override def matches(objectId: ObjectId, effectContext: EffectContext, gameState: GameState): Boolean = {
    CurrentCharacteristics.getStackObject(objectId, gameState).exists(isSpell)
  }
  private def isSpell(stackObject: StackObject): Boolean = {
    stackObject.underlyingObject.isInstanceOf[Card] || stackObject.underlyingObject.isInstanceOf[CopyOfSpell]
  }

  override def getNounPhraseTemplate(cardName: String): NounPhraseTemplate = Nouns.Spell

  override def getAll(effectContext: EffectContext, gameState: GameState): Set[ObjectId] = gameState.gameObjectState.stack.filter(isSpell).map(_.objectId).toSet
}
