package mtg.instructions.nouns

import mtg.core.ObjectId
import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.game.state.GameState
import mtg.text.{VerbNumber, VerbPerson}

object CardName extends IndefiniteNounPhrase[ObjectId] with SingleIdentifyingNounPhrase[ObjectId] {
  override def getText(cardName: String): String = cardName
  override def person: VerbPerson = VerbPerson.Third
  override def number: VerbNumber = VerbNumber.Singular
  override def identify(gameState: GameState, resolutionContext: StackObjectResolutionContext): (ObjectId, StackObjectResolutionContext) = {
    (resolutionContext.sourceId, resolutionContext.addIdentifiedObject(resolutionContext.sourceId))
  }
  override def describes(t: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    t == effectContext.sourceId
  }
}
