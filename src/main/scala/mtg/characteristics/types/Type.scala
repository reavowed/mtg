package mtg.characteristics.types

sealed class Type

object Type {
  case object Land extends Type
  case object Creature extends Type
}
