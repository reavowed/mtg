package mtg.abilities.builder

import mtg.core.ObjectId
import mtg.core.types.{Supertype, Type}
import mtg.effects.condition.Condition
import mtg.instructions.adjectives.{Adjective, SupertypeAdjective}
import mtg.instructions.{CharacteristicChangingVerb, Instruction, SuffixDescriptor, TypePhrase}
import mtg.instructions.nounPhrases.{PluralNoun, SetIdentifyingNounPhrase}
import mtg.instructions.nouns.Noun

object TypeConversions {
  implicit def typeToPhrase(t: Type): TypePhrase = TypePhrase(t)
  implicit def supertypeToAdjective(supertype: Supertype): Adjective = SupertypeAdjective(supertype)
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
