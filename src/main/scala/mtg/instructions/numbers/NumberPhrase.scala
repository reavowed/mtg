package mtg.instructions.numbers

import mtg.game.state.GameState
import mtg.instructions.TextComponent

trait NumberPhrase extends TextComponent {
  def matches(number: Int, gameState: GameState): Boolean
  def matches(number: Option[Int], gameState: GameState): Boolean = matches(number.getOrElse(0), gameState)
}
