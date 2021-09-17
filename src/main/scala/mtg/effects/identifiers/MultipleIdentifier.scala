package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

trait MultipleIdentifier[+T <: ObjectOrPlayer] {
  def getAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext)
  def getText(cardName: String): String
}
