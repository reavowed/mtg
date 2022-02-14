package mtg.game

import mtg.core.zones.ZoneType
import mtg.game.objects._

sealed abstract class Zone(val zoneType: ZoneType)
sealed abstract class TypedZone[ObjectType <: GameObject](zoneType: ZoneType) extends Zone(zoneType)

object Zone {
  sealed abstract class PlayerSpecific[ObjectType <: GameObject](zoneType: ZoneType) extends TypedZone[ObjectType](zoneType) {
    def playerIdentifier: PlayerId
  }
  sealed abstract class Shared[ObjectType <: GameObject](zoneType: ZoneType) extends TypedZone[ObjectType](zoneType)

  sealed trait BasicZone extends TypedZone[BasicGameObject]

  case class Library(playerIdentifier: PlayerId) extends PlayerSpecific[BasicGameObject](ZoneType.Library) with BasicZone
  case class Hand(playerIdentifier: PlayerId) extends PlayerSpecific[BasicGameObject](ZoneType.Hand) with BasicZone
  case object Battlefield extends Shared[PermanentObject](ZoneType.Battlefield)
  case class Graveyard(playerIdentifier: PlayerId) extends PlayerSpecific[BasicGameObject](ZoneType.Graveyard) with BasicZone
  case object Stack extends Shared[StackObject](ZoneType.Stack)
  case object Exile extends Shared[BasicGameObject](ZoneType.Exile) with BasicZone
}

