package mtg.effects.filters

import mtg.game.PlayerId
import mtg.game.state.GameState

object AnyPlayerFilter extends Filter[PlayerId] {
  override def isValid(t: PlayerId, gameState: GameState): Boolean = true
  override def text: String = "player"
}
