package mtg.abilities.builder

import mtg.core.ObjectId
import mtg.core.types.Type
import mtg.effects.condition.Condition
import mtg.instructions.{CharacteristicChangingVerb, Instruction, SuffixDescriptor}
import mtg.instructions.adjectives.Adjective
import mtg.instructions.nounPhrases.{PluralNoun, SetIdentifyingNounPhrase}
import mtg.instructions.nouns.Noun

object TypeConversions extends LowPriorityTypeConversions {
  implicit class TypeExtensions(t: Type) {
    def apply(noun: Noun[ObjectId]): Noun[ObjectId] = typeToAdjective(t)(noun)
    def apply(suffixDescriptor: SuffixDescriptor): Noun[ObjectId] = typeToNoun(t)(suffixDescriptor)
  }
  implicit class ObjectPhraseExtensions(phrase: SetIdentifyingNounPhrase[ObjectId]) {
    def apply(verb: CharacteristicChangingVerb, endCondition: Condition): Instruction = {
      CharacteristicChangingVerb.WithSubjectAndCondition(phrase, verb, endCondition)
    }
  }
  implicit class ObjectNounExtensions(noun: Noun[ObjectId]) {
    def apply(verb: CharacteristicChangingVerb, endCondition: Condition): Instruction = {
      CharacteristicChangingVerb.WithSubjectAndCondition(PluralNoun(noun), verb, endCondition)
    }
    def apply(suffixDescriptor: SuffixDescriptor): Noun[ObjectId] = Noun.WithSuffix(noun, suffixDescriptor)
  }
}

trait LowPriorityTypeConversions {
  implicit def typeToAdjective(t: Type): Adjective = Adjective.TypeAdjective(t)
  implicit def typeToNoun(t: Type): Noun[ObjectId] = Noun.TypeNoun(t)
}
