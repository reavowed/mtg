package mtg.effects.filters.combination

import mtg.effects.filters.base.CharacteristicFilter
import mtg.game.state.{Characteristics, GameState}

case class NegatedCharacteristicFilter(characteristicFilter: CharacteristicFilter) extends CharacteristicFilter {
  override def matches(characteristics: Characteristics, gameState: GameState): Boolean = {
    !characteristicFilter.matches(characteristics, gameState)
  }
  override def getText(cardName: String): String = "non" + characteristicFilter.getText(cardName)
}
