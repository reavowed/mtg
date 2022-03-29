package mtg.instructions.numbers

import mtg.game.state.GameState

case class ConstantOrGreaterMatcher(constant: Int) extends NumberPhrase {
  override def matches(number: Int, gameState: GameState): Boolean = {
    number >= constant
  }
  override def getText(cardName: String): String = s"$constant or greater"
}
