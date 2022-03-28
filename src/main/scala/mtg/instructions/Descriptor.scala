package mtg.instructions

import mtg.effects.EffectContext
import mtg.game.state.GameState

trait Descriptor[ObjectType] extends TextComponent {
  def describes(obj: ObjectType, gameState: GameState, effectContext: EffectContext): Boolean
}
