package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

trait SingleIdentifier[+T <: ObjectOrPlayer] extends MultipleIdentifier[T] {
  def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext)
  def getAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext) = {
    get(gameState, resolutionContext).mapLeft(Seq(_))
  }
  def getText(cardName: String): String
  def getPossessiveText(cardName: String): String = getText(cardName) + "'s"
}
