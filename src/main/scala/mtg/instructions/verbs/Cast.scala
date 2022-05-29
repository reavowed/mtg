package mtg.instructions.verbs

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.{EffectContext, InstructionResolutionContext}
import mtg.game.state.history.HistoryEvent
import mtg.game.state.{GameAction, GameState}
import mtg.instructions.nounPhrases.IndefiniteNounPhrase
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
