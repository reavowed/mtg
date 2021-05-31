package mtg.effects.targets

import mtg.characteristics.types.Type.{Creature, Planeswalker}
import mtg.effects.filters.{AnyPlayerFilter, DisjunctiveFilter, TypeFilter}

case object AnyTarget extends ObjectOrPlayerTargetIdentifier(DisjunctiveFilter(TypeFilter(Creature), TypeFilter(Planeswalker), AnyPlayerFilter)) {
  override def text: String = "any target"
}
