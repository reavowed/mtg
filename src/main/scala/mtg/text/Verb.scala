package mtg.text

trait Verb {
  def inflect(verbInflection: VerbInflection): String
}
object Verb {
  case object Draw extends Verb {
    override def inflect(verbInflection: VerbInflection): String = verbInflection match {
      case VerbInflection.Present(VerbPerson.Third, VerbNumber.Singular) => "draws"
      case _ => "draw"
    }
  }

  abstract class WithSuffix(verb: Verb, suffix: String) extends Verb {
    def inflect(verbInflection: VerbInflection): String = verb.inflect(verbInflection) + " " + suffix
  }
}
