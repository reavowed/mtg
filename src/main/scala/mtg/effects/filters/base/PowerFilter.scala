package mtg.effects.filters.base

import mtg.effects.numbers.NumberMatcher
import mtg.game.state.{Characteristics, GameState}

case class PowerFilter(numberMatcher: NumberMatcher) extends CharacteristicFilter {
  override def matches(characteristics: Characteristics, gameState: GameState): Boolean = numberMatcher.matches(characteristics.power, gameState)
  override def getText(cardName: String): String = s"with power ${numberMatcher.getText(cardName)}"
}
