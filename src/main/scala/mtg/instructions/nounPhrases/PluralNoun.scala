package mtg.instructions.nounPhrases

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.{GrammaticalNumber, GrammaticalPerson}
import mtg.instructions.nouns.ClassNoun

import scala.reflect.ClassTag

case class PluralNoun[T : ClassTag](noun: ClassNoun[T]) extends SetIdentifyingNounPhrase[T] {
  override def getText(cardName: String): String = noun.getPlural(cardName)
  override def person: GrammaticalPerson = GrammaticalPerson.Third
  override def number: GrammaticalNumber = GrammaticalNumber.Plural
  override def identifyAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext) = {
    (noun.getAll(gameState, resolutionContext), resolutionContext)
  }
}
