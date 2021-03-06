package mtg.instructions

import mtg.instructions.grammar.{GrammaticalNumber, GrammaticalPerson, VerbInflection}
import mtg.utils.CaseObjectWithName

trait Verb {
  def inflect(verbInflection: VerbInflection, cardName: String): String
}
object Verb {
  trait Simple extends Verb {
    def root: String
    def thirdPerson: String = root + "s"
    override def inflect(verbInflection: VerbInflection, cardName: String): String = verbInflection match {
      case VerbInflection.Present(GrammaticalPerson.Third, GrammaticalNumber.Singular) => thirdPerson
      case _ => root
    }
  }
  trait RegularCaseObject extends Verb.Simple with CaseObjectWithName {
    override def root: String = name.toLowerCase
  }

  case object Add extends RegularCaseObject
  case object Control extends RegularCaseObject
  case object Deal extends RegularCaseObject
  case object Draw extends RegularCaseObject
  case object Enter extends RegularCaseObject
  case object Gain extends RegularCaseObject
  case object Get extends RegularCaseObject
  case object Put extends RegularCaseObject
  case object Return extends RegularCaseObject
  case object Search extends RegularCaseObject

  abstract class WithSuffix(verb: Verb, suffix: String) extends Verb {
    def inflect(verbInflection: VerbInflection, cardName: String): String = verb.inflect(verbInflection, cardName) + " " + suffix
  }
}
