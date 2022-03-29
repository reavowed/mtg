package mtg.instructions.adjectives

import mtg.core.ObjectId
import mtg.core.types.Type
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.Descriptor
import mtg.instructions.nouns.Noun

trait Adjective extends Descriptor[ObjectId] {
  def apply(noun: Noun[ObjectId]): Noun[ObjectId] = Noun.WithAdjective(this, noun)
}

object Adjective {
  case class TypeAdjective(t: Type) extends Adjective {
    override def getText(cardName: String): String = t.name.toLowerCase
    override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
      gameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(_.characteristics).exists(_.types.contains(t))
    }
  }
}
