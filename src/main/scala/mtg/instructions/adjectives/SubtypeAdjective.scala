package mtg.instructions.adjectives

import mtg.definitions.types.Subtype
import mtg.effects.EffectContext
import mtg.game.state.{Characteristics, GameState}
import mtg.instructions.Descriptor

case class SubtypeAdjective(subtype: Subtype) extends Adjective with Descriptor.CharacteristicDescriptor {
  override def getText(cardName: String): String = subtype.name
  override def describes(characteristics: Characteristics, gameState: GameState, effectContext: EffectContext): Boolean = {
    characteristics.subtypes.contains(subtype)
  }
}
