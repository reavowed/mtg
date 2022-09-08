package mtg.instructions

import mtg.definitions.ObjectId
import mtg.effects.EffectContext
import mtg.game.state.{Characteristics, GameState}

trait Descriptor[ObjectType] extends TextComponent {
  def describes(obj: ObjectType, gameState: GameState, effectContext: EffectContext): Boolean
}

object Descriptor {
  trait CharacteristicDescriptor extends Descriptor[ObjectId] {
    def describes(characteristics: Characteristics, gameState: GameState, effectContext: EffectContext): Boolean
    override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
      gameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(_.characteristics).exists(describes(_, gameState, effectContext))
    }
  }
}
