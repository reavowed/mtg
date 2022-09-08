package mtg.instructions.adjectives

import mtg.definitions.types.Supertype
import mtg.effects.EffectContext
import mtg.game.state.{Characteristics, GameState}
import mtg.instructions.Descriptor

case class SupertypeAdjective(supertype: Supertype) extends Adjective with Descriptor.CharacteristicDescriptor {
  override def getText(cardName: String): String = supertype.name.toLowerCase
  override def describes(characteristics: Characteristics, gameState: GameState, effectContext: EffectContext): Boolean = {
    characteristics.supertypes.contains(supertype)
  }
}
