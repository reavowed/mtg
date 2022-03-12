package mtg.abilities.builder

import mtg._
import mtg.cards.text.InstructionSentence
import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.core.symbols.ManaSymbol
import mtg.effects.condition.ConditionDefinition
import mtg.effects.filters.Filter
import mtg.effects.identifiers.{FilterIdentifier, MultipleIdentifier, SingleIdentifier}
import mtg.instructions.{CreateCharacteristicOrControlChangingContinuousEffectInstruction, Instruction}
import mtg.instructions.actions.{RevealInstruction, ScryInstruction, ShuffleInstruction}
import mtg.instructions.basic.{AddManaInstruction, DealDamageInstruction, DestroyInstruction, DrawACardInstruction, DrawsACardInstruction, ExileInstruction, GainLifeInstruction, PutCountersInstruction, PutIntoHandInstruction, SearchLibraryInstruction}
import mtg.instructions.descriptions.CharacteristicOrControlChangingContinuousEffectDescription
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

  abstract class InstructionsSeqExtension(instructions: Seq[Instruction]) {
    def `then`(instruction: Instruction): InstructionSentence = InstructionSentence.MultiClause(instructions :+ instruction, "then")
  }
  implicit class InstructionExtension(instruction: Instruction) extends InstructionsSeqExtension(Seq(instruction))
  implicit class ThreeInstructionsExtension(instructions: (Instruction, Instruction, Instruction)) extends InstructionsSeqExtension(instructions.productIterator.toSeq.ofType[Instruction])

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
    def drawsACard: Instruction = DrawsACardInstruction(playerIdentifier)
  }

  def searchYourLibraryForA(objectFilter: Filter[ObjectId]): Instruction = SearchLibraryInstruction(objectFilter)
  def reveal(objectIdentifier: SingleIdentifier[ObjectId]): Instruction = RevealInstruction(objectIdentifier)
  def put(objectIdentifier: SingleIdentifier[ObjectId]) = new {
    def intoYourHand: Instruction = PutIntoHandInstruction(objectIdentifier)
  }
  def destroy(objectIdentifier: SingleIdentifier[ObjectId]): Instruction = DestroyInstruction(objectIdentifier)
  def exile(objectIdentifier: SingleIdentifier[ObjectId]): Instruction = ExileInstruction(objectIdentifier)
  def put(number: Int, counterType: CounterType) = new {
    def on(objectIdentifier: SingleIdentifier[ObjectId]): Instruction = PutCountersInstruction(number, counterType, objectIdentifier)
  }
  def shuffle: Instruction = ShuffleInstruction
  def scry(number: Int): Instruction = ScryInstruction(number)
  def drawACard: Instruction = DrawACardInstruction
  def add(manaSymbol: ManaSymbol): Instruction = AddManaInstruction(manaSymbol)
}
