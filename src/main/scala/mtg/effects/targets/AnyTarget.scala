package mtg.effects.targets

import mtg.core.ObjectOrPlayerId
import mtg.core.types.Type
import mtg.effects.filters.AnyPlayerFilter
import mtg.effects.filters.combination.{DisjunctiveFilter, ImplicitPermanentFilter}
import mtg.text.{GrammaticalNumber, NounPhrase}

case object AnyTarget extends TargetIdentifier[ObjectOrPlayerId](DisjunctiveFilter(
    new ImplicitPermanentFilter(Type.Creature),
    new ImplicitPermanentFilter(Type.Planeswalker),
    AnyPlayerFilter)
) {
  override def getNounPhrase(cardName: String): NounPhrase = NounPhrase.Simple("any target", GrammaticalNumber.Singular)
}
