package mtg.abilities.builder

import mtg.core.ObjectId
import mtg.effects.condition.Condition
import mtg.effects.filters.Filter
import mtg.effects.identifiers.FilterIdentifier
import mtg.instructions.descriptions.CharacteristicOrControlChangingContinuousEffectDescription
import mtg.instructions.nounPhrases.SetIdentifyingNounPhrase
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

  implicit class ObjectMultipleIdentifierExtension(objectIdentifier: SetIdentifyingNounPhrase[ObjectId]) {
    def apply(continuousEffectDescriptions: CharacteristicOrControlChangingContinuousEffectDescription*): ContinuousEffectBuilder = ContinuousEffectBuilder(objectIdentifier, continuousEffectDescriptions)
  }

  implicit class ObjectFilterExtension(objectFilter: Filter[ObjectId]) extends ObjectMultipleIdentifierExtension(FilterIdentifier(objectFilter))
}
