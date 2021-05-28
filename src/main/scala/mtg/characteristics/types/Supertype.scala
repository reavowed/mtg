package mtg.characteristics.types

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import mtg.utils.CaseObjectSerializer

@JsonSerialize(using = classOf[CaseObjectSerializer])
sealed class Supertype

object Supertype {
  object Basic extends Supertype
}
