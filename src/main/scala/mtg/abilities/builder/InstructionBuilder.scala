package mtg.abilities.builder

import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.effects.condition.Condition
import mtg.effects.filters.Filter
import mtg.effects.identifiers.{FilterIdentifier, MultipleIdentifier, SingleIdentifier}
import mtg.instructions.basic._
import mtg.instructions.descriptions.CharacteristicOrControlChangingContinuousEffectDescription
import mtg.instructions.nouns.SingleIdentifyingNounPhrase
import mtg.instructions.{CreateCharacteristicOrControlChangingContinuousEffectInstruction, Instruction, IntransitiveInstructionVerb}
import mtg.parts.counters.CounterType

object InstructionBuilder
  extends FilterBuilder
    with IdentifierBuilder
    with TargetBuilder
    with ConditionBuilder
    with NumberBuilder
    with ContinuousEffectBuilder
    with TriggeredAbilityBuilder
    with ParagraphBuilder
{
  case class ContinuousEffectBuilder(objectIdentifier: MultipleIdentifier[ObjectId], continuousEffectDescriptions: Seq[CharacteristicOrControlChangingContinuousEffectDescription]) {
      def until(condition: Condition): Instruction = CreateCharacteristicOrControlChangingContinuousEffectInstruction(objectIdentifier, continuousEffectDescriptions, condition)
  }

  implicit class ObjectMultipleIdentifierExtension(objectIdentifier: MultipleIdentifier[ObjectId]) {
    def apply(continuousEffectDescriptions: CharacteristicOrControlChangingContinuousEffectDescription*): ContinuousEffectBuilder = ContinuousEffectBuilder(objectIdentifier, continuousEffectDescriptions)
  }

  implicit class ObjectFilterExtension(objectFilter: Filter[ObjectId]) extends ObjectMultipleIdentifierExtension(FilterIdentifier(objectFilter))

  def searchYourLibraryForA(objectFilter: Filter[ObjectId]): Instruction = SearchLibraryInstruction(objectFilter)
  def put(objectIdentifier: SingleIdentifier[ObjectId]) = new {
    def intoYourHand: Instruction = PutIntoHandInstruction(objectIdentifier)
  }
  def put(number: Int, counterType: CounterType) = new {
    def on(objectIdentifier: SingleIdentifier[ObjectId]): Instruction = PutCountersInstruction(number, counterType, objectIdentifier)
  }
}
