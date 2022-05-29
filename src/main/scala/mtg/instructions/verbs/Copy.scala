package mtg.instructions.verbs

import mtg.actions.CopySpellAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.game.state.history.HistoryEvent
import mtg.instructions.{InstructionResult, TransitiveEventMatchingVerb, TransitiveInstructionVerb, Verb}

case object Copy extends Verb.RegularCaseObject with TransitiveInstructionVerb[PlayerId, ObjectId] with TransitiveEventMatchingVerb.Simple[PlayerId, ObjectId] {
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
    (CopySpellAction(playerId, objectId), resolutionContext)
  }
  def matchSubjectAndObject(
    eventToMatch: HistoryEvent.ResolvedAction[_],
    gameState: GameState,
    context: InstructionResolutionContext
  ): Option[(PlayerId, ObjectId, InstructionResolutionContext)] = eventToMatch.action match {
    case CopySpellAction(playerId, spellId) => Some((playerId, spellId, context))
    case _ => None
  }
}
