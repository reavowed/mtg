package mtg.game.objects

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import mtg.cards.CardDefinition
import mtg.parts.counters.CounterType
import mtg.game.state.{BasicObjectWithState, Characteristics, GameState, ObjectWithState, PermanentObjectWithState, PermanentStatus, StackObjectWithState}
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId, TypedZone, Zone}

trait GameObject {
  def card: Card
  def objectId: ObjectId
  def zone: Zone
  def counters: Map[CounterType, Int]

  def baseState: ObjectWithState
  def currentState(gameState: GameState): ObjectWithState

  def baseCharacteristics: Characteristics = card.baseCharacteristics
  def currentCharacteristics(gameState: GameState): Characteristics = currentState(gameState).characteristics
  def owner: PlayerId = card.owner

  def isCard: Boolean = true
  def cardDefinition: CardDefinition = card.printing.cardDefinition

  def removeFromCurrentZone(gameObjectState: GameObjectState): GameObjectState
  def add(gameObjectState: GameObjectState, getIndex: Seq[GameObject] => Int): GameObjectState
  def updateCounters(gameObjectState: GameObjectState, f: Map[CounterType, Int] => Map[CounterType, Int]): GameObjectState

  override def toString: String = s"GameObject(${card.baseCharacteristics.name} ${card.printing.set}-${card.printing.collectorNumber}, $objectId)"
}

trait TypedGameObject[T <: GameObject] extends GameObject { this: T =>
  def zone: TypedZone[T]
  def updateCounters(newCounters: Map[CounterType, Int]): T

  def removeFromCurrentZone(gameObjectState: GameObjectState): GameObjectState = zone.updateState(gameObjectState, _.filter(_ != this))
  def add(gameObjectState: GameObjectState, getIndex: Seq[GameObject] => Int): GameObjectState = zone.updateState(gameObjectState, s => s.insertAtIndex(this, getIndex(s)))
  def update(gameObjectState: GameObjectState, f: T => T): GameObjectState = {
    zone.updateState(gameObjectState, _.map(o => if (o == this) f(o) else o))
  }
  def updateCounters(gameObjectState: GameObjectState, f: Map[CounterType, Int] => Map[CounterType, Int]): GameObjectState = {
    update(gameObjectState, o => updateCounters(f(o.counters)))
  }
}

@JsonSerialize(using = classOf[GameObject.Serializer])
case class BasicGameObject(card: Card, objectId: ObjectId, zone: TypedZone[BasicGameObject], counters: Map[CounterType, Int]) extends TypedGameObject[BasicGameObject] {
  override def updateCounters(newCounters: Map[CounterType, Int]): BasicGameObject = copy(counters = newCounters)
  override def baseState: ObjectWithState = BasicObjectWithState(this, baseCharacteristics)
  def currentState(gameState: GameState): BasicObjectWithState = gameState.gameObjectState.derivedState.basicStates(objectId)
}
object BasicGameObject {
  def apply(card: Card, objectId: ObjectId, zone: TypedZone[BasicGameObject]): BasicGameObject = BasicGameObject(card, objectId, zone, Map.empty)
}

case class PermanentObject(card: Card, objectId: ObjectId, defaultController: PlayerId, counters: Map[CounterType, Int], status: PermanentStatus, markedDamage: Int) extends TypedGameObject[PermanentObject] {
  val zone: Zone.Battlefield.type = Zone.Battlefield
  override def updateCounters(newCounters: Map[CounterType, Int]): PermanentObject = copy(counters = newCounters)
  def updatePermanentStatus(f: PermanentStatus => PermanentStatus): PermanentObject = copy(status = f(status))
  def updateMarkedDamage(f: Int => Int): PermanentObject = copy(markedDamage = f(markedDamage))
  override def baseState: PermanentObjectWithState = PermanentObjectWithState(this, baseCharacteristics, defaultController)
  def currentState(gameState: GameState): PermanentObjectWithState = gameState.gameObjectState.derivedState.permanentStates(objectId)
}
object PermanentObject {
  def apply(card: Card, objectId: ObjectId, defaultController: PlayerId): PermanentObject = PermanentObject(card, objectId, defaultController, Map.empty, PermanentStatus(false, false, false, false), 0)
}

case class StackObject(card: Card, objectId: ObjectId, defaultController: PlayerId, targets: Seq[ObjectOrPlayer], counters: Map[CounterType, Int]) extends TypedGameObject[StackObject] {
  val zone: Zone.Stack.type = Zone.Stack
  override def updateCounters(newCounters: Map[CounterType, Int]): StackObject = copy(counters = newCounters)
  override def baseState: StackObjectWithState = StackObjectWithState(this, baseCharacteristics, defaultController)
  def currentState(gameState: GameState): StackObjectWithState = gameState.gameObjectState.derivedState.spellStates(objectId)
  def addTarget(objectOrPlayer: ObjectOrPlayer): StackObject = copy(targets = targets :+ objectOrPlayer)
}
object StackObject {
  def apply(card: Card, objectId: ObjectId, defaultController: PlayerId): StackObject = StackObject(card, objectId, defaultController, Nil, Map.empty)
}

object GameObject {
  class Serializer extends JsonSerializer[GameObject] {
    override def serialize(value: GameObject, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeNumber(value.objectId.sequentialId)
    }
  }
}
