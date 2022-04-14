package mtg.instructions

sealed trait VerbPerson
object VerbPerson {
  case object Second extends VerbPerson
  case object Third extends VerbPerson
}

sealed trait VerbNumber
object VerbNumber {
  case object Singular extends VerbNumber
  case object Plural extends VerbNumber
}

sealed trait VerbInflection
object VerbInflection {
  case object Imperative extends VerbInflection
  case object Infinitive extends VerbInflection
  case class Present(person: VerbPerson, number: VerbNumber) extends VerbInflection
}
