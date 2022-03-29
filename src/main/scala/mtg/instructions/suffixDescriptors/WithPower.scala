package mtg.instructions.suffixDescriptors

import mtg.effects.EffectContext
import mtg.effects.numbers.NumberMatcher
import mtg.game.state.{Characteristics, GameState}
import mtg.instructions.{Descriptor, SuffixDescriptor}

case class WithPower(numberMatcher: NumberMatcher) extends SuffixDescriptor with Descriptor.CharacteristicDescriptor {
  override def describes(characteristics: Characteristics, gameState: GameState, effectContext: EffectContext): Boolean = numberMatcher.matches(characteristics.power, gameState)

  override def getText(cardName: String): String = s"with power ${numberMatcher.getText(cardName)}"
}
