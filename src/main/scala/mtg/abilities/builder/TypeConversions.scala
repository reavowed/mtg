package mtg.abilities.builder

import mtg.core.ObjectId
import mtg.core.types.Type
import mtg.effects.condition.Condition
import mtg.instructions.{CharacteristicChangingVerb, Instruction, SuffixDescriptor, TypePhrase}
import mtg.instructions.nounPhrases.{PluralNoun, SetIdentifyingNounPhrase}
import mtg.instructions.nouns.Noun

object TypeConversions {
  implicit def typeToPhrase(t: Type): TypePhrase = TypePhrase(t)
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
