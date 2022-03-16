package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.text.{VerbNumber, VerbPerson}

trait SingleIdentifier[+T] extends MultipleIdentifier[T] {
  def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext)
  def getAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext) = {
    get(gameState, resolutionContext).mapLeft(Seq(_))
  }
  def person: VerbPerson = VerbPerson.Third
  def number: VerbNumber = VerbNumber.Singular
}
