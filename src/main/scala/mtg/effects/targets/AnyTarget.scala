package mtg.effects.targets

import mtg.characteristics.types.Type.{Creature, Planeswalker}
import mtg.effects.filters.base.TypeFilter
import mtg.effects.filters.combination.{DisjunctiveFilter, ImplicitPermanentFilter}
import mtg.effects.filters.{AnyPlayerFilter, base}

case object AnyTarget extends ObjectOrPlayerTargetIdentifier(DisjunctiveFilter(
    new ImplicitPermanentFilter(TypeFilter(Creature)),
    base.TypeFilter(Planeswalker),
    AnyPlayerFilter)
) {
  override def text: String = "any target"
}
