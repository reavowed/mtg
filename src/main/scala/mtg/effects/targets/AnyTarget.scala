package mtg.effects.targets

import mtg.core.ObjectOrPlayerId
import mtg.core.types.Type
import mtg.effects.filters.AnyPlayerFilter
import mtg.effects.filters.combination.{Or, ImplicitPermanentFilter}

case object AnyTarget extends TargetIdentifier[ObjectOrPlayerId](Or(
    new ImplicitPermanentFilter(Type.Creature),
    new ImplicitPermanentFilter(Type.Planeswalker),
    AnyPlayerFilter)
) {
  override def getText(cardName: String): String = "any target"
}
