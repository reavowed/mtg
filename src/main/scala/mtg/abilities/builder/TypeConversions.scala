package mtg.abilities.builder

import mtg.core.types.Type
import mtg.instructions.adjectives.Adjective

object TypeConversions {
  implicit def typeToAdjective(t: Type): Adjective = Adjective.TypeAdjective(t)
}
