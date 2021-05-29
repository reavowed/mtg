package mtg.effects.identifiers

import mtg.effects.ResolutionContext
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

trait Identifier[+T <: ObjectOrPlayer] {
  def get(gameState: GameState, resolutionContext: ResolutionContext): (T, ResolutionContext)
  def getText(cardName: String): String
}
