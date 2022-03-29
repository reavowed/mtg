package mtg.instructions.nounPhrases

import mtg.core.ObjectId
import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.game.state.GameState
import mtg.text.VerbPerson

object CardName extends IndefiniteNounPhrase[ObjectId] with SingleIdentifyingNounPhrase[ObjectId] {
  override def getText(cardName: String): String = cardName

  override def person: VerbPerson = VerbPerson.Third

  override def identifySingle(gameState: GameState, resolutionContext: StackObjectResolutionContext): (ObjectId, StackObjectResolutionContext) = {
    (resolutionContext.cardNameObjectId, resolutionContext.addIdentifiedObject(resolutionContext.cardNameObjectId))
  }

  override def describes(t: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    t == effectContext.cardNameObjectId
  }
}
