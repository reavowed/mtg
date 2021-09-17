package mtg.effects.numbers

import mtg.game.state.GameState

trait NumberMatcher {
  def matches(number: Int, gameState: GameState): Boolean
  def matches(number: Option[Int], gameState: GameState): Boolean = matches(number.getOrElse(0), gameState)
  def getText(cardName: String): String
}
