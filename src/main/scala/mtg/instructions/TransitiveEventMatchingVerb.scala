package mtg.instructions

import mtg.definitions.ObjectOrPlayerId
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.game.state.history.HistoryEvent
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.IndefiniteNounPhrase

trait TransitiveEventMatchingVerb[SubjectType <: ObjectOrPlayerId, ObjectType <: ObjectOrPlayerId] extends Verb {
  def matchEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, context: InstructionResolutionContext, subjectPhrase: IndefiniteNounPhrase[SubjectType], objectPhrase: IndefiniteNounPhrase[ObjectType]): Option[InstructionResolutionContext]
  def apply(objectPhrase: IndefiniteNounPhrase[ObjectType]): IntransitiveEventMatchingVerb[SubjectType] = {
    TransitiveEventMatchingVerb.WithObject(this, objectPhrase)
  }
}

object TransitiveEventMatchingVerb {
  trait Simple[SubjectType <: ObjectOrPlayerId, ObjectType <: ObjectOrPlayerId] extends TransitiveEventMatchingVerb[SubjectType, ObjectType] {
    def matchSubjectAndObject(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, context: InstructionResolutionContext): Option[(SubjectType, ObjectType, InstructionResolutionContext)]
    def matchEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, context: InstructionResolutionContext, subjectPhrase: IndefiniteNounPhrase[SubjectType], objectPhrase: IndefiniteNounPhrase[ObjectType]): Option[InstructionResolutionContext] = {
      for {
        (subject, obj, contextAfterSubject) <- matchSubjectAndObject(eventToMatch, gameState, context)
        if subjectPhrase.describes(subject, gameState, context)
        contextWithSubject = contextAfterSubject.addIdentifiedObject(subject)
        if objectPhrase.describes(obj, gameState, contextWithSubject)
        contextWithObject = contextWithSubject.addIdentifiedObject(obj)
      } yield contextWithObject
    }
  }

  case class WithObject[SubjectType <: ObjectOrPlayerId, ObjectType <: ObjectOrPlayerId](
      transitiveEventMatchingVerb: TransitiveEventMatchingVerb[SubjectType, ObjectType],
      objectPhrase: IndefiniteNounPhrase[ObjectType])
    extends IntransitiveEventMatchingVerb[SubjectType]
  {
    override def inflect(verbInflection: VerbInflection, cardName: String): String = transitiveEventMatchingVerb.inflect(verbInflection, cardName) + " " + objectPhrase.getText(cardName)
    override def matchEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, context: InstructionResolutionContext, subjectPhrase: IndefiniteNounPhrase[SubjectType]): Option[InstructionResolutionContext] = {
      transitiveEventMatchingVerb.matchEvent(eventToMatch, gameState, context, subjectPhrase, objectPhrase)
    }
  }
}

