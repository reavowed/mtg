package mtg.effects.targets

import mtg.characteristics.types.Type.{Creature, Planeswalker}
import mtg.effects.filters.AnyPlayerFilter
import mtg.effects.filters.combination.{DisjunctiveFilter, ImplicitPermanentFilter}

case object AnyTarget extends ObjectOrPlayerTargetIdentifier(DisjunctiveFilter(
    new ImplicitPermanentFilter(Creature),
    new ImplicitPermanentFilter(Planeswalker),
    AnyPlayerFilter)
) {
  override def text: String = "any target"
}
