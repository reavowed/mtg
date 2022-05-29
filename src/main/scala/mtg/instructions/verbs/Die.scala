package mtg.instructions.verbs

import mtg.actions.moveZone.MoveToGraveyardAction
import mtg.core.ObjectId
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.game.state.history.HistoryEvent.ResolvedAction
import mtg.instructions.{IntransitiveEventMatchingVerb, Verb}

case object Die extends Verb.RegularCaseObject with IntransitiveEventMatchingVerb.Simple[ObjectId] {
  override def looksBackInTime: Boolean = true
  override def matchSubject(eventToMatch: ResolvedAction[_], gameState: GameState, context: InstructionResolutionContext): Option[(ObjectId, InstructionResolutionContext)] = eventToMatch match {
    case ResolvedAction(MoveToGraveyardAction(objectId), Some(_), _) => Some((objectId, context))
    case _ => None
  }
}
