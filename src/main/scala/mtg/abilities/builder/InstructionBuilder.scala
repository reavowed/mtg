package mtg.abilities.builder

import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.effects.condition.ConditionDefinition
import mtg.effects.filters.Filter
import mtg.effects.identifiers.{FilterIdentifier, MultipleIdentifier, SingleIdentifier}
import mtg.instructions.basic._
import mtg.instructions.descriptions.CharacteristicOrControlChangingContinuousEffectDescription
import mtg.instructions.{CreateCharacteristicOrControlChangingContinuousEffectInstruction, Instruction}
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
  case class DealInstructionBuilder(objectIdentifier: SingleIdentifier[ObjectId], amount: Int) {
      def damageTo(recipientIdentifier: SingleIdentifier[ObjectOrPlayerId]): Instruction = DealDamageInstruction(objectIdentifier, recipientIdentifier, amount)
  }
  case class ContinuousEffectBuilder(objectIdentifier: MultipleIdentifier[ObjectId], continuousEffectDescriptions: Seq[CharacteristicOrControlChangingContinuousEffectDescription]) {
      def until(conditionDefinition: ConditionDefinition): Instruction = CreateCharacteristicOrControlChangingContinuousEffectInstruction(objectIdentifier, continuousEffectDescriptions, conditionDefinition)
  }

  implicit class ObjectSingleIdentifierExtension(objectIdentifier: SingleIdentifier[ObjectId]) {
    def deals(amount: Int): DealInstructionBuilder = DealInstructionBuilder(objectIdentifier, amount)
  }
  implicit class ObjectMultipleIdentifierExtension(objectIdentifier: MultipleIdentifier[ObjectId]) {
    def apply(continuousEffectDescriptions: CharacteristicOrControlChangingContinuousEffectDescription*): ContinuousEffectBuilder = ContinuousEffectBuilder(objectIdentifier, continuousEffectDescriptions)
  }

  implicit class ObjectFilterExtension(objectFilter: Filter[ObjectId]) extends ObjectMultipleIdentifierExtension(FilterIdentifier(objectFilter))

  implicit class PlayerIdentifierExtension(playerIdentifier: SingleIdentifier[PlayerId]) {
    def gain(amount: Int) = new {
      def life: Instruction = GainLifeInstruction(playerIdentifier, amount)
    }
  }

  def searchYourLibraryForA(objectFilter: Filter[ObjectId]): Instruction = SearchLibraryInstruction(objectFilter)
  def put(objectIdentifier: SingleIdentifier[ObjectId]) = new {
    def intoYourHand: Instruction = PutIntoHandInstruction(objectIdentifier)
  }
  def put(number: Int, counterType: CounterType) = new {
    def on(objectIdentifier: SingleIdentifier[ObjectId]): Instruction = PutCountersInstruction(number, counterType, objectIdentifier)
  }
}
