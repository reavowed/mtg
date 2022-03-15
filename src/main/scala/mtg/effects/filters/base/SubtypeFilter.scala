package mtg.effects.filters.base

import mtg.core.types.Subtype
import mtg.game.state.{Characteristics, GameState}

case class SubtypeFilter(subtype: Subtype) extends CharacteristicFilter {
  override def matches(characteristics: Characteristics, gameState: GameState): Boolean = {
    characteristics.subtypes.contains(subtype)
  }
  override def getText(cardName: String): String = subtype.name
}
