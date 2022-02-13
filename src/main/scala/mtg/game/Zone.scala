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
  def newObjectForZone(previousObjectState: ObjectWithState, playerMoving: PlayerId, newObjectId: ObjectId): ObjectType
}

object Zone {
  sealed abstract class PlayerSpecific[ObjectType <: GameObject](zoneType: ZoneType) extends TypedZone[ObjectType](zoneType) {
    def playerIdentifier: PlayerId
    def stateMapLens: Lens[GameObjectState, Map[PlayerId, Seq[ObjectType]]]
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
  }
  case class Graveyard(playerIdentifier: PlayerId) extends PlayerSpecific[BasicGameObject](ZoneType.Graveyard) with BasicZone {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerId, Seq[BasicGameObject]]] = Focus[GameObjectState](_.graveyards)
  }
  case object Stack extends Shared[StackObject](ZoneType.Stack) {
    override def newObjectForZone(previousObjectState: ObjectWithState, playerMoving: PlayerId, newObjectId: ObjectId): StackObject = {
      StackObject(previousObjectState.gameObject.underlyingObject, newObjectId, playerMoving)
    }
  }
  case object Exile extends Shared[BasicGameObject](ZoneType.Exile) with BasicZone
}

