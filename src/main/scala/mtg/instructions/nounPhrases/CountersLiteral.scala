package mtg.instructions.nounPhrases

import mtg.effects.EffectContext
import mtg.instructions.grammar.{GrammaticalNumber, GrammaticalPerson}
import mtg.instructions.nouns.Noun
import mtg.parts.Counter
import mtg.utils.TextUtils._

case class CountersLiteral(numberOfCounters: Int, counterType: Counter) extends StaticSingleIdentifyingNounPhrase[Map[Counter, Int]] {
  override def identify(effectContext: EffectContext): Map[Counter, Int] = {
    Map(counterType -> numberOfCounters)
  }
  override def number: GrammaticalNumber = GrammaticalNumber(numberOfCounters)
  override def person: GrammaticalPerson = GrammaticalPerson.Third
  override def getText(cardName: String): String = {
    val numberWord = numberOfCounters match {
      case 1 => getArticle(counterType.description)
      case 2 => "two"
      case n => n.toString
    }
    Seq(numberWord, counterType.description, Noun.Counter.getText(cardName, number)).mkString(" ")
  }
}
