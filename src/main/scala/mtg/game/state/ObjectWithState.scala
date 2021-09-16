package mtg.game.state

import mtg.abilities.AbilityDefinition
import mtg.cards.text.{ModalEffectParagraph, SimpleSpellEffectParagraph, SpellEffectParagraph}
import mtg.effects.EffectContext
import mtg.game.PlayerId
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
  def getEffectContext(gameState: GameState): EffectContext = new EffectContext(controller, gameObject.underlyingObject.getSourceName(gameState))

  def applicableEffectParagraphs: Seq[SimpleSpellEffectParagraph] = {
    @tailrec
    def getApplicableEffectParagraphs(chosenModes: Seq[Int], remainingParagraphs: Seq[SpellEffectParagraph], resultsSoFar: Seq[SimpleSpellEffectParagraph]): Seq[SimpleSpellEffectParagraph] = {
      remainingParagraphs match {
        case (simpleSpellEffectParagraph: SimpleSpellEffectParagraph) +: tail =>
          getApplicableEffectParagraphs(chosenModes, tail, resultsSoFar :+ simpleSpellEffectParagraph)
        case (modalEffectParagraph: ModalEffectParagraph) +: tail =>
          getApplicableEffectParagraphs(chosenModes.tail, tail, resultsSoFar :+ modalEffectParagraph.modes(chosenModes.head))
        case Nil =>
          resultsSoFar
      }
    }
    getApplicableEffectParagraphs(gameObject.chosenModes, characteristics.rulesText.ofType[SpellEffectParagraph], Nil)
  }
}

object ObjectWithState {
  def initial(gameObject: BasicGameObject): ObjectWithState = BasicObjectWithState(gameObject, gameObject.baseCharacteristics)
}
