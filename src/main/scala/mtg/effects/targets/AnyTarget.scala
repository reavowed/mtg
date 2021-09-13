package mtg.effects.targets

import mtg.characteristics.types.Type.{Creature, Planeswalker}
import mtg.effects.filters.AnyPlayerFilter
import mtg.effects.filters.combination.{DisjunctiveFilter, ImplicitPermanentFilter}
import mtg.game.ObjectOrPlayer

case object AnyTarget extends TargetIdentifier[ObjectOrPlayer](DisjunctiveFilter(
    new ImplicitPermanentFilter(Creature),
    new ImplicitPermanentFilter(Planeswalker),
    AnyPlayerFilter)
) {
  override def getText(cardName: String): String = "any target"
}
