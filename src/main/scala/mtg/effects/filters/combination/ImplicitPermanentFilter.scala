package mtg.effects.filters.combination

import mtg.effects.filters.Filter
import mtg.effects.filters.base.PermanentFilter
import mtg.game.ObjectId

class ImplicitPermanentFilter(
    val baseSubfilters: Filter[ObjectId]*)
  extends CompoundFilter[ObjectId](
    baseSubfilters :+ PermanentFilter)
{
  override def text: String = baseSubfilters.map(_.text).mkString(" ")
}
