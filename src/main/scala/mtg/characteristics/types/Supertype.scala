package mtg.characteristics.types

import mtg.utils.CaseObject

sealed class Supertype extends CaseObject

object Supertype {
  object Basic extends Supertype
}
