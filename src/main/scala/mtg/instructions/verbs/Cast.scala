package mtg.instructions.verbs

import mtg.definitions.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.game.state.history.HistoryEvent
import mtg.instructions.{TransitiveEventMatchingVerb, Verb}
import mtg.stack.adding.FinishCasting

case object Cast extends Verb.RegularCaseObject with TransitiveEventMatchingVerb.Simple[PlayerId, ObjectId] {
  def matchSubjectAndObject(
    eventToMatch: HistoryEvent.ResolvedAction[_],
    gameState: GameState,
    context: InstructionResolutionContext
  ): Option[(PlayerId, ObjectId, InstructionResolutionContext)] = eventToMatch.action match {
    case FinishCasting(playerId, spellId) => Some((playerId, spellId, context))
    case _ => None
  }
}
