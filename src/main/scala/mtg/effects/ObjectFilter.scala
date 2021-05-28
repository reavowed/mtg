package mtg.effects

import mtg.characteristics.types.Supertype.Basic
import mtg.characteristics.types.Type.Land
import mtg.game.state.ObjectWithState

case class ObjectFilter(predicate: ObjectWithState => Boolean, description: String)
object ObjectFilter {
  def basicLand: ObjectFilter = ObjectFilter(o => o.characteristics.superTypes.contains(Basic) && o.characteristics.types.contains(Land), "basic land")
}
