package mtg.game.state

import mtg.abilities.{AbilityDefinition, SpellAbility}
import mtg.cards.text.{InstructionParagraph, ModalInstructionParagraph, SimpleInstructionParagraph}
import mtg.core.PlayerId
import mtg.game.objects.{BasicGameObject, GameObject, PermanentObject, StackObject}

import scala.annotation.tailrec

sealed abstract class ObjectWithState {
  def gameObject: GameObject
  def characteristics: Characteristics
  def controllerOrOwner: PlayerId
  def updateCharacteristics(f: Characteristics => Characteristics): ObjectWithState
  def addAbility(abilityDefinition: AbilityDefinition): ObjectWithState
}
sealed abstract class TypedObjectWithState[T <: ObjectWithState] extends ObjectWithState {
  def updateCharacteristics(f: Characteristics => Characteristics): T
  def addAbility(abilityDefinition: AbilityDefinition): T = {
    updateCharacteristics(c => c.copy(abilities = c.abilities :+ abilityDefinition))
  }
}

case class BasicObjectWithState(
    gameObject: BasicGameObject,
    characteristics: Characteristics)
  extends TypedObjectWithState[BasicObjectWithState]
{
  override def controllerOrOwner: PlayerId = gameObject.owner
  override def updateCharacteristics(f: Characteristics => Characteristics): BasicObjectWithState = copy(characteristics = f(characteristics))
}

case class PermanentObjectWithState(
    gameObject: PermanentObject,
    characteristics: Characteristics,
    controller: PlayerId)
  extends TypedObjectWithState[PermanentObjectWithState]
{
  override def controllerOrOwner: PlayerId = controller
  override def updateCharacteristics(f: Characteristics => Characteristics): PermanentObjectWithState = copy(characteristics = f(characteristics))
}

case class StackObjectWithState(
    gameObject: StackObject,
    characteristics: Characteristics,
    controller: PlayerId)
  extends TypedObjectWithState[StackObjectWithState]
{
  override def controllerOrOwner: PlayerId = controller
  override def updateCharacteristics(f: Characteristics => Characteristics): StackObjectWithState = copy(characteristics = f(characteristics))

  def applicableInstructionParagraphs: Seq[SimpleInstructionParagraph] = {
    @tailrec
    def getApplicableInstructionParagraphs(chosenModes: Seq[Int], remainingParagraphs: Seq[InstructionParagraph], resultsSoFar: Seq[SimpleInstructionParagraph]): Seq[SimpleInstructionParagraph] = {
      remainingParagraphs match {
        case (simpleSpellEffectParagraph: SimpleInstructionParagraph) +: tail =>
          getApplicableInstructionParagraphs(chosenModes, tail, resultsSoFar :+ simpleSpellEffectParagraph)
        case (modalEffectParagraph: ModalInstructionParagraph) +: tail =>
          getApplicableInstructionParagraphs(chosenModes.tail, tail, resultsSoFar :+ modalEffectParagraph.modes(chosenModes.head))
        case Nil =>
          resultsSoFar
      }
    }
    getApplicableInstructionParagraphs(gameObject.chosenModes, characteristics.instructionParagraphs, Nil)
  }
}

object ObjectWithState {
  def initial(gameObject: BasicGameObject): ObjectWithState = BasicObjectWithState(gameObject, gameObject.baseCharacteristics)
}
