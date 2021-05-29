package mtg.effects.filters

import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

case class DisjunctiveFilter[T <: ObjectOrPlayer](innerFilters: Filter[T]*) extends Filter[T] {
  override def isValid(t: T, gameState: GameState): Boolean = {
    innerFilters.exists(_.isValid(t, gameState))
  }
  override def text: String = innerFilters.map(_.text).mkString(" or ")
}
