package mtg.effects.filters.base

import mtg.characteristics.types.Type
import mtg.game.state.{Characteristics, GameState}

case class TypeFilter(`type`: Type) extends CharacteristicFilter {
  override def matches(characteristics: Characteristics, gameState: GameState): Boolean = {
    characteristics.types.contains(`type`)
  }

  override def getText(cardName: String): String = `type`.name.toLowerCase
}
