package mtg.instructions.verbs

import mtg.actions.CopySpellAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.{EffectContext, InstructionResolutionContext}
import mtg.game.state.history.HistoryEvent
import mtg.game.state.{GameAction, GameState}
import mtg.instructions.nounPhrases.IndefiniteNounPhrase
import mtg.instructions.{InstructionResult, TransitiveEventMatchingVerb, TransitiveInstructionVerb, Verb}

case object Copy extends Verb.RegularCaseObject with TransitiveInstructionVerb[PlayerId, ObjectId] with TransitiveEventMatchingVerb {
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
    (CopySpellAction(playerId, objectId), resolutionContext)
  }
  override def matchesEvent(
    eventToMatch: HistoryEvent.ResolvedAction[_],
    gameState: GameState,
    effectContext: EffectContext,
    playerPhrase: IndefiniteNounPhrase[PlayerId],
    objectPhrase: IndefiniteNounPhrase[ObjectId]
  ): Boolean = eventToMatch.action match {
    case CopySpellAction(playerId, spellId) if playerPhrase.describes(playerId, gameState, effectContext) && objectPhrase.describes(spellId, gameState, effectContext) =>
      true
    case _ =>
      false
  }
}
