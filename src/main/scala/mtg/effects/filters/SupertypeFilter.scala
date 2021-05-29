package mtg.effects.filters

import mtg.characteristics.types.Supertype
import mtg.game.state.{Characteristics, GameState}

case class SupertypeFilter(supertype: Supertype) extends CharacteristicFilter {
  override def isValid(characteristics: Characteristics, gameState: GameState): Boolean = {
    characteristics.superTypes.contains(supertype)
  }
  override def text: String = supertype.name.toLowerCase
}
