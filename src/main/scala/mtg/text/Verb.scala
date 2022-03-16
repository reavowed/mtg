package mtg.text

import mtg.utils.CaseObjectWithName

trait Verb {
  def inflect(verbInflection: VerbInflection, cardName: String): String
}
object Verb {
  trait RegularCaseObject extends Verb with CaseObjectWithName {
    override def inflect(verbInflection: VerbInflection, cardName: String): String = verbInflection match {
      case VerbInflection.Present(VerbPerson.Third, VerbNumber.Singular) => name.toLowerCase + "s"
      case _ => name.toLowerCase
    }
  }

  case object Draw extends RegularCaseObject

  abstract class WithSuffix(verb: Verb, suffix: String) extends Verb {
    def inflect(verbInflection: VerbInflection, cardName: String): String = verb.inflect(verbInflection, cardName) + " " + suffix
  }
}
