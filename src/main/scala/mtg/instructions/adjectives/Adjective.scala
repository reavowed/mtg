package mtg.instructions.adjectives

import mtg.core.ObjectId
import mtg.core.types.Type
import mtg.effects.EffectContext
import mtg.game.state.{Characteristics, GameState}
import mtg.instructions.Descriptor
import mtg.instructions.nouns.Noun

trait Adjective extends Descriptor[ObjectId] {
  def apply(noun: Noun[ObjectId]): Noun[ObjectId] = Noun.WithAdjective(this, noun)
}

object Adjective {
  case class TypeAdjective(t: Type) extends Adjective with Descriptor.CharacteristicDescriptor {
    override def getText(cardName: String): String = t.name.toLowerCase
    override def describes(characteristics: Characteristics, gameState: GameState, effectContext: EffectContext): Boolean = {
      characteristics.types.contains(t)
    }
  }
  case class Non(adjective: Adjective) extends Adjective {
    override def getText(cardName: String): String = "non" + adjective.getText(cardName)
    override def describes(obj: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
      !adjective.describes(obj, gameState, effectContext)
    }

  }
}
