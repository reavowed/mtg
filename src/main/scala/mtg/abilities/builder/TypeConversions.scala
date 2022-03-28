package mtg.abilities.builder

import mtg.core.types.Type
import mtg.instructions.adjectives.Adjective
import mtg.instructions.nouns.Noun

object TypeConversions {
  implicit class TypeExtensions(t: Type) {
    def apply(noun: Noun): Noun = Noun.WithAdjective(Adjective.TypeAdjective(t), noun)
  }
}
