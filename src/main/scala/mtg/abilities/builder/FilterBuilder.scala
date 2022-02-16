package mtg.abilities.builder

import mtg.characteristics.types.{Supertype, Type}
import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.effects.filters.base._
import mtg.effects.filters.combination.{ImplicitPermanentFilter, NegatedCharacteristicFilter, PrefixFilter, SuffixFilter}
import mtg.effects.filters.{Filter, PartialFilter}
import mtg.effects.identifiers.StaticIdentifier
import mtg.effects.numbers.NumberMatcher

trait FilterBuilder extends FilterBuilder.LowPriority {
  implicit class PlayerIdentifierExtensions(playerIdentifier: StaticIdentifier[PlayerId]) {
    def control: PartialFilter[ObjectId] = ControllerFilter(playerIdentifier)
  }
  implicit class FilterExtensions[T <: ObjectOrPlayerId](filter: Filter[T]) {
    def apply(suffixFilters: PartialFilter[T]*): Filter[T] = SuffixFilter(filter, suffixFilters)
  }
  implicit class TypeFilterExtensions(t: Type) extends FilterExtensions(typeToPermanentFilter(t))

  implicit def typeToFilter(t: Type): PartialFilter[ObjectId] = TypeFilter(t)
  implicit def supertypeToFilter(supertype: Supertype): PartialFilter[ObjectId] = SupertypeFilter(supertype)

  def card(filters: PartialFilter[ObjectId]*): Filter[ObjectId] = new PrefixFilter[ObjectId](filters, CardFilter)
  def permanent(filters: PartialFilter[ObjectId]*): Filter[ObjectId] = new PrefixFilter[ObjectId](filters, PermanentFilter)

  def non(t: Type) = NegatedCharacteristicFilter(TypeFilter(t))

  def withPower(numberMatcher: NumberMatcher) = PowerFilter(numberMatcher)
}

object FilterBuilder {
  trait LowPriority {
    // allow referring to e.g. a "creature", meaning a permanent with the creature type
    implicit def typeToPermanentFilter(t: Type): Filter[ObjectId] = new ImplicitPermanentFilter(t)
  }
}
