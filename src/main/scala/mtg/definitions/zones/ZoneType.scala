package mtg.definitions.zones

sealed abstract class ZoneType

object ZoneType {
  case object Library extends ZoneType
  case object Hand extends ZoneType
  case object Graveyard extends ZoneType
  case object Battlefield extends ZoneType
  case object Stack extends ZoneType
  case object Exile extends ZoneType
  case object Sideboard extends ZoneType
  // TODO: Support command zone?
}
