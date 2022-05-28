package mtg.instructions.grammar

sealed trait GrammaticalNumber

object GrammaticalNumber {
  case object Singular extends GrammaticalNumber
  case object Plural extends GrammaticalNumber

  def apply(n: Int): GrammaticalNumber = if (n == 1) Singular else Plural
}
