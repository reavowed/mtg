package mtg.effects.number

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState

trait NumberIdentifier {
  def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): Int
  def getText(followingWord: String): String
  def isSingular: Boolean
}
