package mtg.effects.targets

import mtg.characteristics.types.Type.{Creature, Planeswalker}
import mtg.effects.filters.{AnyPlayerFilter, DisjunctiveFilter, Filter, TypeFilter}
import mtg.game.ObjectOrPlayer

case object AnyTarget extends TargetIdentifier {
  override def filter: Filter[ObjectOrPlayer] = DisjunctiveFilter(TypeFilter(Creature), TypeFilter(Planeswalker), AnyPlayerFilter)
  override def text: String = "any target"
}
