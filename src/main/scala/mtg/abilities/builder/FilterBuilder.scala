package mtg.abilities.builder

import mtg.characteristics.types.Supertype.Basic
import mtg.characteristics.types.Type
import mtg.characteristics.types.Type.Land
import mtg.effects.filters.{CompoundCharacteristicFilter, Filter, SupertypeFilter, TypeFilter}
import mtg.game.ObjectId

trait FilterBuilder {
  def basicLand: Filter[ObjectId] = CompoundCharacteristicFilter(SupertypeFilter(Basic), TypeFilter(Land))
  implicit def typeFilter(t: Type): TypeFilter = TypeFilter(t)
}
