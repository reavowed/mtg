package mtg.core.types

import mtg.utils.CaseObjectWithName

sealed class Supertype extends CaseObjectWithName

object Supertype {
  object Basic extends Supertype
}
