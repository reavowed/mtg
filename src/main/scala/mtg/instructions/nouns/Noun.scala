package mtg.instructions.nouns

import mtg.instructions.grammar.GrammaticalNumber
import mtg.utils.CaseObjectWithName

trait Noun {
  def getSingular(cardName: String): String
  def getPlural(cardName: String): String = getSingular(cardName) + "s"

  def getText(cardName: String, number: GrammaticalNumber) = number match {
    case GrammaticalNumber.Singular => getSingular(cardName)
    case GrammaticalNumber.Plural => getPlural(cardName)
  }
}

object Noun {
  trait RegularCaseObject extends Noun with CaseObjectWithName {
    override def getSingular(cardName: String): String = name.toLowerCase
  }

  case object Counter extends RegularCaseObject
}
