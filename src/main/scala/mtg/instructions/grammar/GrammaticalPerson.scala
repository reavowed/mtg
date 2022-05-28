package mtg.instructions.grammar

sealed trait GrammaticalPerson

object GrammaticalPerson {
  case object Second extends GrammaticalPerson
  case object Third extends GrammaticalPerson
}
