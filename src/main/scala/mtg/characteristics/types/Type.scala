package mtg.characteristics.types

sealed trait Type

object Type {
  sealed trait SpellType extends Type
  sealed trait PermanentType extends SpellType

  case object Land extends Type
  case object Instant extends SpellType
  case object Creature extends PermanentType
}
