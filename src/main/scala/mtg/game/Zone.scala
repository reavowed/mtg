package mtg.game

import mtg.core.PlayerId
import mtg.core.zones.ZoneType

sealed abstract class Zone(val zoneType: ZoneType)

object Zone {
  // A basic zone is one in which objects do not carry any special information. Best defined by the non-basic zones,
  // which are the battlefield (where objects carry status and damage information), and the stack (targets etc.)
  sealed abstract class BasicZone(zoneType: ZoneType) extends Zone(zoneType)

  case class Library(playerIdentifier: PlayerId) extends BasicZone(ZoneType.Library)
  case class Hand(playerIdentifier: PlayerId) extends BasicZone(ZoneType.Hand)
  case class Graveyard(playerIdentifier: PlayerId) extends BasicZone(ZoneType.Graveyard)
  case object Battlefield extends Zone(ZoneType.Battlefield)
  case object Stack extends Zone(ZoneType.Stack)
  case object Exile extends BasicZone(ZoneType.Exile)
}

