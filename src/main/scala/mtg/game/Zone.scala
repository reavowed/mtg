package mtg.game

import monocle.{Focus, Lens}
import mtg.core.zones.ZoneType
import mtg.game.objects._
import mtg.game.state.{GameState, ObjectWithState, StackObjectWithState}
import mtg.utils.AtGuaranteed

sealed abstract class Zone(val zoneType: ZoneType) {
  def newObjectForZone(previousObjectState: ObjectWithState, playerMoving: PlayerId, newObjectId: ObjectId): GameObject
}

sealed abstract class TypedZone[ObjectType <: GameObject](zoneType: ZoneType) extends Zone(zoneType) {
  def stateLens: Lens[GameObjectState, Seq[ObjectType]]
  def updateState(gameObjectState: GameObjectState, f: Seq[ObjectType] => Seq[ObjectType]): GameObjectState = {
    stateLens.modify(f)(gameObjectState)
  }
  def newObjectForZone(previousObjectState: ObjectWithState, playerMoving: PlayerId, newObjectId: ObjectId): ObjectType
}

object Zone {
  sealed abstract class PlayerSpecific[ObjectType <: GameObject](zoneType: ZoneType) extends TypedZone[ObjectType](zoneType) {
    def playerIdentifier: PlayerId
    def stateMapLens: Lens[GameObjectState, Map[PlayerId, Seq[ObjectType]]]
    override def stateLens: Lens[GameObjectState, Seq[ObjectType]] = stateMapLens.at(playerIdentifier)(AtGuaranteed.apply)
  }
  sealed abstract class Shared[ObjectType <: GameObject](zoneType: ZoneType) extends TypedZone[ObjectType](zoneType)

  trait BasicZone extends TypedZone[BasicGameObject] {
    override def newObjectForZone(previousObjectState: ObjectWithState, playerMoving: PlayerId, newObjectId: ObjectId): BasicGameObject = {
      BasicGameObject(previousObjectState.gameObject.underlyingObject, newObjectId, this)
    }
  }

  case class Library(playerIdentifier: PlayerId) extends PlayerSpecific[BasicGameObject](ZoneType.Library) with BasicZone {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerId, Seq[BasicGameObject]]] = Focus[GameObjectState](_.libraries)

  }
  case class Hand(playerIdentifier: PlayerId) extends PlayerSpecific[BasicGameObject](ZoneType.Hand) with BasicZone {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerId, Seq[BasicGameObject]]] = Focus[GameObjectState](_.hands)
  }
  case object Battlefield extends Shared[PermanentObject](ZoneType.Battlefield) {
    override def newObjectForZone(previousObjectState: ObjectWithState, playerMoving: PlayerId, newObjectId: ObjectId): PermanentObject = {
      val controller = previousObjectState.asOptionalInstanceOf[StackObjectWithState].map(_.controller).getOrElse(playerMoving)
      PermanentObject(previousObjectState.gameObject.underlyingObject, newObjectId, controller)
    }
    override def stateLens: Lens[GameObjectState, Seq[PermanentObject]] = Focus[GameObjectState](_.battlefield)
  }
  case class Graveyard(playerIdentifier: PlayerId) extends PlayerSpecific[BasicGameObject](ZoneType.Graveyard) with BasicZone {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerId, Seq[BasicGameObject]]] = Focus[GameObjectState](_.graveyards)
  }
  case object Stack extends Shared[StackObject](ZoneType.Stack) {
    override def newObjectForZone(previousObjectState: ObjectWithState, playerMoving: PlayerId, newObjectId: ObjectId): StackObject = {
      StackObject(previousObjectState.gameObject.underlyingObject, newObjectId, playerMoving)
    }
    override def stateLens: Lens[GameObjectState, Seq[StackObject]] = Focus[GameObjectState](_.stack)
  }
  case object Exile extends Shared[BasicGameObject](ZoneType.Exile) with BasicZone {
    override def stateLens: Lens[GameObjectState, Seq[BasicGameObject]] = Focus[GameObjectState](_.exile)
  }
}

