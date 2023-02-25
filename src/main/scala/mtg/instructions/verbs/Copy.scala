package mtg.instructions.verbs

import mtg.actions.CopySpellAction
import mtg.definitions.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.game.state.history.HistoryEvent
import mtg.instructions.{InstructionAction, MonotransitiveInstructionVerb, TransitiveEventMatchingVerb, Verb}

case object Copy extends Verb.RegularCaseObject with MonotransitiveInstructionVerb[PlayerId, ObjectId] with TransitiveEventMatchingVerb.Simple[PlayerId, ObjectId] {
  override def resolve(playerId: PlayerId, objectId: ObjectId): InstructionAction = {
    CopySpellAction(playerId, objectId)
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
