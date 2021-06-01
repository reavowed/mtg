package mtg.game

import monocle.{Focus, Lens}
import mtg.game.objects._
import mtg.game.state.{GameState, ObjectWithState, StackObjectWithState}
import mtg.utils.AtGuaranteed

sealed abstract class ZoneType
object ZoneType {
  case object Library extends ZoneType
  case object Hand extends ZoneType
  case object Battlefield extends ZoneType
  case object Graveyard extends ZoneType
  case object Stack extends ZoneType
  case object Exile extends ZoneType
  case object Sideboard extends ZoneType
}

sealed abstract class Zone(val zoneType: ZoneType) {
  def getState(gameObjectState: GameObjectState): Seq[GameObject]
  def getState(gameState: GameState): Seq[GameObject]
  def newObjectForZone(previousObjectState: ObjectWithState, playerMoving: PlayerId, newObjectId: ObjectId): GameObject
}

sealed abstract class TypedZone[ObjectType <: GameObject](zoneType: ZoneType) extends Zone(zoneType) {
  def stateLens: Lens[GameObjectState, Seq[ObjectType]]
  def getState(gameState: GameState): Seq[ObjectType] = getState(gameState.gameObjectState)
  def getState(gameObjectState: GameObjectState): Seq[ObjectType] = stateLens.get(gameObjectState)
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
      BasicGameObject(previousObjectState.gameObject.card, newObjectId, this)
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
      PermanentObject(previousObjectState.gameObject.card, newObjectId, controller)
    }
    override def stateLens: Lens[GameObjectState, Seq[PermanentObject]] = Focus[GameObjectState](_.battlefield)
  }
  case class Graveyard(playerIdentifier: PlayerId) extends PlayerSpecific[BasicGameObject](ZoneType.Graveyard) with BasicZone {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerId, Seq[BasicGameObject]]] = Focus[GameObjectState](_.graveyards)
  }
  case object Stack extends Shared[StackObject](ZoneType.Stack) {
    override def newObjectForZone(previousObjectState: ObjectWithState, playerMoving: PlayerId, newObjectId: ObjectId): StackObject = {
      StackObject(previousObjectState.gameObject.card, newObjectId, playerMoving)
    }
    override def stateLens: Lens[GameObjectState, Seq[StackObject]] = Focus[GameObjectState](_.stack)
  }
  case class Sideboard(playerIdentifier: PlayerId) extends PlayerSpecific[BasicGameObject](ZoneType.Hand) with BasicZone {
    override def stateMapLens: Lens[GameObjectState, Map[PlayerId, Seq[BasicGameObject]]] = Focus[GameObjectState](_.sideboards)
  }
  case object Exile extends Shared[BasicGameObject](ZoneType.Exile) with BasicZone {
    override def stateLens: Lens[GameObjectState, Seq[BasicGameObject]] = Focus[GameObjectState](_.exile)
  }
}

