package mtg.abilities.builder

import mtg.characteristics.types.{Supertype, Type}
import mtg.effects.filters.base.{CardFilter, PermanentFilter, SupertypeFilter, TypeFilter}
import mtg.effects.filters.combination.{ImplicitPermanentFilter, NegatedCharacteristicFilter, PrefixFilter}
import mtg.effects.filters.{Filter, PartialFilter}
import mtg.game.ObjectId

trait FilterBuilder extends FilterBuilder.LowPriority {
  implicit def typeToFilter(t: Type): PartialFilter[ObjectId] = TypeFilter(t)
  implicit def supertypeToFilter(supertype: Supertype): PartialFilter[ObjectId] = SupertypeFilter(supertype)

  def card(filters: PartialFilter[ObjectId]*): Filter[ObjectId] = new PrefixFilter[ObjectId](filters, CardFilter)
  def permanent(filters: PartialFilter[ObjectId]*): Filter[ObjectId] = new PrefixFilter[ObjectId](filters, PermanentFilter)

  def non(t: Type) = NegatedCharacteristicFilter(TypeFilter(t))
}

object FilterBuilder {
  trait LowPriority {
    // allow referring to e.g. a "creature", meaning a permanent with the creature type
    implicit def typeToPermanentFilter(t: Type): Filter[ObjectId] = new ImplicitPermanentFilter(t)
  }
}
