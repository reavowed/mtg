package mtg.game.objects

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import mtg.game.Zone.BasicZone
import mtg.game._
import mtg.game.state._
import mtg.parts.counters.CounterType

trait GameObject {
  def underlyingObject: UnderlyingObject
  def objectId: ObjectId
  def zone: Zone
  def counters: Map[CounterType, Int]

  def baseState: ObjectWithState
  def currentState(gameState: GameState): ObjectWithState

  def baseCharacteristics: Characteristics = underlyingObject.baseCharacteristics
  def currentCharacteristics(gameState: GameState): Characteristics = currentState(gameState).characteristics
  def owner: PlayerId = underlyingObject.owner

  def isCard: Boolean = true

  def updateCounters(f: Map[CounterType, Int] => Map[CounterType, Int]): GameObject

  override def toString: String = s"GameObject($underlyingObject, $objectId)"
}

trait TypedGameObject[T <: GameObject] extends GameObject { this: T =>
  def updateCounters(newCounters: Map[CounterType, Int]): T
  def updateCounters(f: Map[CounterType, Int] => Map[CounterType, Int]): T = updateCounters(f(counters))
}

@JsonSerialize(using = classOf[GameObject.Serializer])
case class BasicGameObject(underlyingObject: UnderlyingObject, objectId: ObjectId, zone: BasicZone, counters: Map[CounterType, Int]) extends TypedGameObject[BasicGameObject] {
  override def updateCounters(newCounters: Map[CounterType, Int]): BasicGameObject = copy(counters = newCounters)
  override def baseState: ObjectWithState = BasicObjectWithState(this, baseCharacteristics)
  def currentState(gameState: GameState): BasicObjectWithState = gameState.gameObjectState.derivedState.basicStates(objectId)
}
object BasicGameObject {
  def apply(underlyingObject: UnderlyingObject, objectId: ObjectId, zone: BasicZone): BasicGameObject = BasicGameObject(underlyingObject, objectId, zone, Map.empty)
}

case class PermanentObject(underlyingObject: UnderlyingObject, objectId: ObjectId, defaultController: PlayerId, counters: Map[CounterType, Int], status: PermanentStatus, markedDamage: Int) extends TypedGameObject[PermanentObject] {
  val zone: Zone.Battlefield.type = Zone.Battlefield
  override def updateCounters(newCounters: Map[CounterType, Int]): PermanentObject = copy(counters = newCounters)
  def updatePermanentStatus(f: PermanentStatus => PermanentStatus): PermanentObject = copy(status = f(status))
  def updateMarkedDamage(f: Int => Int): PermanentObject = copy(markedDamage = f(markedDamage))
  override def baseState: PermanentObjectWithState = PermanentObjectWithState(this, baseCharacteristics, defaultController)
  def currentState(gameState: GameState): PermanentObjectWithState = gameState.gameObjectState.derivedState.permanentStates(objectId)
}
object PermanentObject {
  def apply(underlyingObject: UnderlyingObject, objectId: ObjectId, defaultController: PlayerId): PermanentObject = PermanentObject(underlyingObject, objectId, defaultController, Map.empty, PermanentStatus(false, false, false, false), 0)
}

case class StackObject(underlyingObject: UnderlyingObject, objectId: ObjectId, defaultController: PlayerId, chosenModes: Seq[Int], targets: Seq[ObjectOrPlayer], counters: Map[CounterType, Int]) extends TypedGameObject[StackObject] {
  val zone: Zone.Stack.type = Zone.Stack
  override def updateCounters(newCounters: Map[CounterType, Int]): StackObject = copy(counters = newCounters)
  override def baseState: StackObjectWithState = StackObjectWithState(this, baseCharacteristics, defaultController)
  def currentState(gameState: GameState): StackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(objectId)
  def addTarget(objectOrPlayer: ObjectOrPlayer): StackObject = copy(targets = targets :+ objectOrPlayer)
  def addMode(modeIndex: Int): StackObject = copy(chosenModes = chosenModes :+ modeIndex)
}
object StackObject {
  def apply(underlyingObject: UnderlyingObject, objectId: ObjectId, defaultController: PlayerId): StackObject = StackObject(underlyingObject, objectId, defaultController, Nil, Nil, Map.empty)
}

object GameObject {
  class Serializer extends JsonSerializer[GameObject] {
    override def serialize(value: GameObject, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeNumber(value.objectId.sequentialId)
    }
  }
}
