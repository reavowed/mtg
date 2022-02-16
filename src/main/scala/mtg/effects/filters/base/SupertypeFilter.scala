package mtg.effects.filters.base

import mtg.core.types.Supertype
import mtg.game.state.{Characteristics, GameState}

case class SupertypeFilter(supertype: Supertype) extends CharacteristicFilter {
  override def matches(characteristics: Characteristics, gameState: GameState): Boolean = {
    characteristics.superTypes.contains(supertype)
  }

  override def getText(cardName: String): String = supertype.name.toLowerCase
}
