package mtg.instructions.nounPhrases

import mtg.instructions.TextComponent
import mtg.instructions.grammar.{GrammaticalNumber, GrammaticalPerson}

trait NounPhrase extends TextComponent {
  def person: GrammaticalPerson
  def number: GrammaticalNumber
}
