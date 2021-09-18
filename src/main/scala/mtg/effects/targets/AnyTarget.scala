package mtg.effects.targets

import mtg.characteristics.types.Type.{Creature, Planeswalker}
import mtg.effects.filters.AnyPlayerFilter
import mtg.effects.filters.combination.{DisjunctiveFilter, ImplicitPermanentFilter}
import mtg.game.ObjectOrPlayer
import mtg.text.{GrammaticalNumber, NounPhrase}

case object AnyTarget extends TargetIdentifier[ObjectOrPlayer](DisjunctiveFilter(
    new ImplicitPermanentFilter(Creature),
    new ImplicitPermanentFilter(Planeswalker),
    AnyPlayerFilter)
) {
  override def getNounPhrase(cardName: String): NounPhrase = NounPhrase.Simple("any target", GrammaticalNumber.Singular)
}
