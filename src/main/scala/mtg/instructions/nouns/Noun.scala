package mtg.instructions.nouns

import mtg.utils.CaseObjectWithName

trait Noun {
  def getSingular(cardName: String): String
  def getPlural(cardName: String): String = getSingular(cardName) + "s"
}

object Noun {
  trait RegularCaseObject extends Noun with CaseObjectWithName {
    override def getSingular(cardName: String): String = name.toLowerCase
  }
}
