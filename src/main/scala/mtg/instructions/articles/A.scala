package mtg.instructions.articles

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.nouns.{IndefiniteNounPhrase, Noun}
import mtg.text.{VerbNumber, VerbPerson}
import mtg.utils.TextUtils._

case class A(noun: Noun[ObjectId]) extends IndefiniteNounPhrase[ObjectId] {
  override def getText(cardName: String): String = noun.getSingular(cardName).withArticle
  override def person: VerbPerson = VerbPerson.Third
  override def number: VerbNumber = VerbNumber.Singular
  override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    noun.describes(objectId, gameState, effectContext)
  }
}
