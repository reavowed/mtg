package mtg.instructions.articles

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.nouns.{IndefiniteNounPhrase, Noun}

case class A(noun: Noun) extends IndefiniteNounPhrase[ObjectId] {
  override def getText(cardName: String): String = "a " + noun.singular
  override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    noun.describes(objectId, gameState, effectContext)
  }
}