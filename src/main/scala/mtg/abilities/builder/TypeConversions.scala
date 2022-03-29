package mtg.abilities.builder

import mtg.core.ObjectId
import mtg.core.types.Type
import mtg.instructions.SuffixDescriptor
import mtg.instructions.adjectives.Adjective
import mtg.instructions.nouns.Noun

object TypeConversions extends LowPriorityTypeConversions {
  implicit class TypeExtensions(t: Type) {
    def apply(noun: Noun[ObjectId]): Noun[ObjectId] = typeToAdjective(t)(noun)
    def apply(suffixDescriptor: SuffixDescriptor): Noun[ObjectId] = typeToNoun(t)(suffixDescriptor)
  }
}

trait LowPriorityTypeConversions {
  implicit def typeToAdjective(t: Type): Adjective = Adjective.TypeAdjective(t)
  implicit def typeToNoun(t: Type): Noun[ObjectId] = Noun.TypeNoun(t)
}
