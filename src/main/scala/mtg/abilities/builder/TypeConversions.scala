package mtg.abilities.builder

import mtg.definitions.ObjectId
import mtg.definitions.types.{Subtype, Supertype, Type}
import mtg.effects.condition.Condition
import mtg.instructions.adjectives.{Adjective, SubtypeAdjective, SupertypeAdjective}
import mtg.instructions.nounPhrases.{PluralNoun, SetIdentifyingNounPhrase}
import mtg.instructions.nouns.ClassNoun
import mtg.instructions.{CharacteristicChangingVerb, Instruction, SuffixDescriptor, TypePhrase}

object TypeConversions {
  implicit def typeToPhrase(t: Type): TypePhrase = TypePhrase(t)
  implicit def supertypeToAdjective(supertype: Supertype): Adjective = SupertypeAdjective(supertype)
  implicit def subtypeToAdjective(subtype: Subtype): Adjective = SubtypeAdjective(subtype)
  implicit class ObjectPhraseExtensions(phrase: SetIdentifyingNounPhrase[ObjectId]) {
    def apply(verb: CharacteristicChangingVerb, endCondition: Condition): Instruction = {
      CharacteristicChangingVerb.WithSubjectAndCondition(phrase, verb, endCondition)
    }
  }
  implicit class ObjectNounExtensions(noun: ClassNoun[ObjectId]) {
    def apply(verb: CharacteristicChangingVerb, endCondition: Condition): Instruction = {
      CharacteristicChangingVerb.WithSubjectAndCondition(PluralNoun(noun), verb, endCondition)
    }
    def apply(suffixDescriptor: SuffixDescriptor): ClassNoun[ObjectId] = ClassNoun.WithSuffix(noun, suffixDescriptor)
  }
}
