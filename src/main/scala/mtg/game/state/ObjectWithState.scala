package mtg.game.state

import mtg.abilities.AbilityDefinition
import mtg.game.PlayerId
import mtg.game.objects.{BasicGameObject, GameObject, PermanentObject, StackObject}

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
}

object ObjectWithState {
  def initial(gameObject: BasicGameObject): ObjectWithState = BasicObjectWithState(gameObject, gameObject.baseCharacteristics)
}
