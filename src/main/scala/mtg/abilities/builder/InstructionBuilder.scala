package mtg.abilities.builder

import mtg.core.ObjectId
import mtg.effects.condition.Condition
import mtg.instructions.descriptions.CharacteristicOrControlChangingContinuousEffectDescription
import mtg.instructions.nounPhrases.{PluralNoun, SetIdentifyingNounPhrase}
import mtg.instructions.nouns.Noun
import mtg.instructions.{CreateCharacteristicOrControlChangingContinuousEffectInstruction, Instruction}

object InstructionBuilder
  extends FilterBuilder
    with IdentifierBuilder
    with ConditionBuilder
    with NumberBuilder
    with ContinuousEffectBuilder
    with ParagraphBuilder
{
  case class ContinuousEffectBuilder(objectIdentifier: SetIdentifyingNounPhrase[ObjectId], continuousEffectDescriptions: Seq[CharacteristicOrControlChangingContinuousEffectDescription]) {
      def until(condition: Condition): Instruction = CreateCharacteristicOrControlChangingContinuousEffectInstruction(objectIdentifier, continuousEffectDescriptions, condition)
  }

  implicit class ObjectSetPhraseExtension(objectPhrase: SetIdentifyingNounPhrase[ObjectId]) {
    def apply(continuousEffectDescriptions: CharacteristicOrControlChangingContinuousEffectDescription*): ContinuousEffectBuilder = ContinuousEffectBuilder(objectPhrase, continuousEffectDescriptions)
  }
  implicit class NounExtension(noun: Noun[ObjectId]) extends ObjectSetPhraseExtension(PluralNoun(noun))
}
