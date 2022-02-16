package mtg.effects.identifiers

import mtg.core.ObjectOrPlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState

trait SingleIdentifier[+T <: ObjectOrPlayerId] extends MultipleIdentifier[T] {
  def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext)
  def getAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext) = {
    get(gameState, resolutionContext).mapLeft(Seq(_))
  }
}
