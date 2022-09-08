package mtg.instructions

import mtg.definitions.ObjectOrPlayerId
import mtg.effects.InstructionResolutionContext
import mtg.effects.condition.Condition
import mtg.game.state.GameState
import mtg.game.state.history.HistoryEvent
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.IndefiniteNounPhrase

trait IntransitiveEventMatchingVerb[SubjectType <: ObjectOrPlayerId] extends Verb {
  def looksBackInTime: Boolean = false
  def matchEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, context: InstructionResolutionContext, subjectPhrase: IndefiniteNounPhrase[SubjectType]): Option[InstructionResolutionContext]
}

object IntransitiveEventMatchingVerb {
  trait Simple[SubjectType <: ObjectOrPlayerId] extends IntransitiveEventMatchingVerb[SubjectType] {
    def matchSubject(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, context: InstructionResolutionContext): Option[(SubjectType, InstructionResolutionContext)]
    def matchEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, context: InstructionResolutionContext, subjectPhrase: IndefiniteNounPhrase[SubjectType]): Option[InstructionResolutionContext] = {
      for {
        (subject, contextAfterSubject) <- matchSubject(eventToMatch, gameState, context)
        if subjectPhrase.describes(subject, gameState, context)
        contextWithSubject = contextAfterSubject.addIdentifiedObject(subject)
      } yield (contextWithSubject)
    }
  }

  case class WithSubject[SubjectType <: ObjectOrPlayerId](subjectPhrase: IndefiniteNounPhrase[SubjectType], verb: IntransitiveEventMatchingVerb[SubjectType]) extends Condition {
    override def looksBackInTime: Boolean = verb.looksBackInTime
    override def matchEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, context: InstructionResolutionContext): Option[InstructionResolutionContext] = {
      verb.matchEvent(eventToMatch, gameState, context, subjectPhrase)
    }
    override def getText(cardName: String): String = {
      subjectPhrase.getText(cardName) + " " + verb.inflect(VerbInflection.Present(subjectPhrase), cardName)
    }
  }
}
