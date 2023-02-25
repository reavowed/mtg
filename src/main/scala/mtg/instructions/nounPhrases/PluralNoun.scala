package mtg.instructions.nounPhrases

import mtg.instructions.InstructionAction
import mtg.instructions.grammar.{GrammaticalNumber, GrammaticalPerson}
import mtg.instructions.nouns.ClassNoun

import scala.reflect.ClassTag

case class PluralNoun[T : ClassTag](noun: ClassNoun[T]) extends SetIdentifyingNounPhrase[T] {
  override def getText(cardName: String): String = noun.getPlural(cardName)
  override def person: GrammaticalPerson = GrammaticalPerson.Third
  override def number: GrammaticalNumber = GrammaticalNumber.Plural

  def identifyAll: InstructionAction.WithResult[Seq[T]] = InstructionAction.WithResult.withoutContextUpdate { (resolutionContext, gameState) =>
    noun.getAll(gameState, resolutionContext)
  }
}
