package mtg.characteristics.types

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import mtg.utils.CaseObjectSerializer

@JsonSerialize(using = classOf[CaseObjectSerializer])
sealed trait Type

object Type {
  sealed trait SpellType extends Type
  sealed trait PermanentType extends SpellType

  case object Land extends Type
  case object Instant extends SpellType
  case object Creature extends PermanentType
}
