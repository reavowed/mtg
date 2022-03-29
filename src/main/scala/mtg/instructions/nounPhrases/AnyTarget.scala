package mtg.instructions.nounPhrases

import mtg.core.ObjectOrPlayerId
import mtg.core.types.Type
import mtg.effects.filters.AnyPlayerFilter
import mtg.effects.filters.combination.{ImplicitPermanentFilter, Or}

object AnyTarget extends Target[ObjectOrPlayerId](Or(
  new ImplicitPermanentFilter(Type.Creature),
  new ImplicitPermanentFilter(Type.Planeswalker),
  AnyPlayerFilter)
) {
  override def getText(cardName: String): String = "any target"
}
