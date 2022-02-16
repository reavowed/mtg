package mtg.core.types

import mtg.utils.CaseObjectWithName

sealed trait Type extends CaseObjectWithName

object Type {
  trait InstantOrSorcery extends Type

  case object Artifact extends Type
  case object Creature extends Type
  case object Enchantment extends Type
  case object Instant extends InstantOrSorcery
  case object Land extends Type
  case object Planeswalker extends Type
  case object Sorcery extends InstantOrSorcery
  // Unsupported (so far): Conspiracy, Dungeon, Phenomenon, Plane, Scheme, Tribal, Vanguard
}
