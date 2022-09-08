package mtg.instructions.articles

import mtg.definitions.ObjectId
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.grammar.{GrammaticalNumber, GrammaticalPerson}
import mtg.instructions.nounPhrases.IndefiniteNounPhrase
import mtg.instructions.nouns.ClassNoun
import mtg.utils.TextUtils._

case class A(noun: ClassNoun[ObjectId]) extends IndefiniteNounPhrase[ObjectId] {
  override def getText(cardName: String): String = noun.getSingular(cardName).withArticle
  override def person: GrammaticalPerson = GrammaticalPerson.Third
  override def number: GrammaticalNumber = GrammaticalNumber.Singular
  override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    noun.getAll(gameState, effectContext).contains(objectId)
  }
}
