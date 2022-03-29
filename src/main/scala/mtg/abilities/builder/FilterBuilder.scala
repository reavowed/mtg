package mtg.abilities.builder

import mtg.core.types.{Supertype, Type}
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.filters.base._
import mtg.effects.filters.combination.{ImplicitPermanentFilter, NegatedCharacteristicFilter, PrefixFilter, SuffixFilter}
import mtg.effects.filters.{Filter, PartialFilter}
import mtg.effects.numbers.NumberMatcher
import mtg.instructions.nounPhrases.StaticSingleIdentifyingNounPhrase
import mtg.instructions.suffixDescriptors.WithPower

trait FilterBuilder extends FilterBuilder.LowPriority {
  implicit class PlayerIdentifierExtensions(playerIdentifier: StaticSingleIdentifyingNounPhrase[PlayerId]) {
    def control: PartialFilter[ObjectId] = ControllerFilter(playerIdentifier)
  }

  implicit def typeToFilter(t: Type): PartialFilter[ObjectId] = TypeFilter(t)
  implicit def supertypeToFilter(supertype: Supertype): PartialFilter[ObjectId] = SupertypeFilter(supertype)

  def card(filters: PartialFilter[ObjectId]*): Filter[ObjectId] = new PrefixFilter[ObjectId](filters, CardFilter)
  def permanent(filters: PartialFilter[ObjectId]*): Filter[ObjectId] = new PrefixFilter[ObjectId](filters, PermanentFilter)

  def non(t: Type) = NegatedCharacteristicFilter(TypeFilter(t))

  def withPower(numberMatcher: NumberMatcher) = WithPower(numberMatcher)
}

object FilterBuilder {
  trait LowPriority {
    // allow referring to e.g. a "creature", meaning a permanent with the creature type
    implicit def typeToPermanentFilter(t: Type): Filter[ObjectId] = new ImplicitPermanentFilter(t)
  }
}
