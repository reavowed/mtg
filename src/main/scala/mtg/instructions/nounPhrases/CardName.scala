package mtg.instructions.nounPhrases

import mtg.definitions.ObjectId
import mtg.effects.{EffectContext, InstructionResolutionContext}
import mtg.game.state.GameState
import mtg.instructions.InstructionAction
import mtg.instructions.grammar.GrammaticalPerson

object CardName extends IndefiniteNounPhrase[ObjectId] with StaticSingleIdentifyingNounPhrase[ObjectId] {
  override def getText(cardName: String): String = cardName

  override def person: GrammaticalPerson = GrammaticalPerson.Third

  override def identify(effectContext: EffectContext): ObjectId = {
    effectContext.cardNameObjectId
  }

  override def identifySingle: InstructionAction.WithResult[ObjectId] = { (resolutionContext: InstructionResolutionContext) =>
    (resolutionContext.cardNameObjectId, resolutionContext.addIdentifiedObject(resolutionContext.cardNameObjectId))
  }

  override def describes(t: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    t == effectContext.cardNameObjectId
  }
}
