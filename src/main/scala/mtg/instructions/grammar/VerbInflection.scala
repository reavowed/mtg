package mtg.instructions.grammar

import mtg.instructions.nounPhrases.NounPhrase

sealed trait VerbInflection
object VerbInflection {
  case object Imperative extends VerbInflection
  case object Infinitive extends VerbInflection
  case class Present(person: GrammaticalPerson, number: GrammaticalNumber) extends VerbInflection
  object Present {
    def apply(nounPhrase: NounPhrase): VerbInflection = Present(nounPhrase.person, nounPhrase.number)
  }
}
