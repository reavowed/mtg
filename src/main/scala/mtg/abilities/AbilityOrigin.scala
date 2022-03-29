package mtg.abilities

sealed trait AbilityOrigin
object AbilityOrigin {
  case object Printed extends AbilityOrigin
  case object Granted extends AbilityOrigin
}
