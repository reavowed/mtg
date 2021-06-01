package mtg.abilities.builder

import mtg.characteristics.types.{Supertype, Type}
import mtg.effects.filters.base.{CardFilter, CharacteristicFilter, PermanentFilter, SupertypeFilter, TypeFilter}
import mtg.effects.filters.combination.{CompoundFilter, ImplicitPermanentFilter, NegatedCharacteristicFilter}

trait FilterBuilder {
  implicit class CharacteristicFilterExtensions(characteristicFilter: CharacteristicFilter) {
    def apply(t: Type) = new ImplicitPermanentFilter(characteristicFilter, TypeFilter(t))
    def permanent = new CompoundFilter(Seq(characteristicFilter, PermanentFilter))
  }
  implicit class SupertypeExtensions(s: Supertype) extends CharacteristicFilterExtensions(SupertypeFilter(s))

  implicit class ImplicitPermanentFilterExtensions(compoundFilter: ImplicitPermanentFilter) {
    def card = new CompoundFilter(compoundFilter.baseSubfilters :+ CardFilter)
    def permanent = new CompoundFilter(compoundFilter.baseSubfilters :+ PermanentFilter)
  }

  def non(t: Type) = NegatedCharacteristicFilter(TypeFilter(t))

  implicit def typeToPermanentFilter(t: Type): ImplicitPermanentFilter = new ImplicitPermanentFilter(TypeFilter(t))
}
