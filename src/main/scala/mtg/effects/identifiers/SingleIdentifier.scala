package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.text.VerbNumber

trait SingleIdentifier[+T] extends MultipleIdentifier[T] {
  def identifySingle(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext)
  def identifyAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext) = {
    identifySingle(gameState, resolutionContext).mapLeft(Seq(_))
  }
  override def number: VerbNumber = VerbNumber.Singular
}
