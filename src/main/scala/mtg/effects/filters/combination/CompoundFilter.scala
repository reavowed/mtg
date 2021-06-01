package mtg.effects.filters.combination

import mtg.effects.filters.Filter
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

class CompoundFilter[T <: ObjectOrPlayer](val subfilters: Seq[Filter[T]]) extends Filter[T] {
  override def isValid(t: T, gameState: GameState): Boolean = subfilters.forall(_.isValid(t, gameState))
  override def text: String = subfilters.map(_.text).mkString(" ")
}
