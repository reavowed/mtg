package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

trait Identifier[+T <: ObjectOrPlayer] {
  def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext)
  def getText(cardName: String): String
  def getPossessiveText(cardName: String): String = getText(cardName) + "'s"
}
