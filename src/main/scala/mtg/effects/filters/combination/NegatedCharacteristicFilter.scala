package mtg.effects.filters.combination

import mtg.effects.filters.base.CharacteristicFilter
import mtg.game.state.{Characteristics, GameState}

case class NegatedCharacteristicFilter(characteristicFilter: CharacteristicFilter) extends CharacteristicFilter {
  override def isValid(characteristics: Characteristics, gameState: GameState): Boolean = {
    !characteristicFilter.isValid(characteristics, gameState)
  }
  override def text: String = "non" + characteristicFilter.text
}
