package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.effects.filters.Filter
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

case class FilterIdentifier[T <: ObjectOrPlayer](filter: Filter[T]) extends MultipleIdentifier[T] {
  override def getAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext) = ???
  override def getText(cardName: String): String = ???
}
