package mtg.effects.filters.base

import mtg.characteristics.types.Type
import mtg.game.state.{Characteristics, GameState}

case class TypeFilter(`type`: Type) extends CharacteristicFilter {
  override def isValid(characteristics: Characteristics, gameState: GameState): Boolean = {
    characteristics.types.contains(`type`)
  }

  override def text: String = `type`.name.toLowerCase
}