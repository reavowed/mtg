package mtg.instructions

import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.state.GameState
import mtg.game.state.history.HistoryEvent
import mtg.instructions.nounPhrases.IndefiniteNounPhrase

trait IntransitiveEventMatchingVerb[SubjectType] extends Verb {
  def looksBackInTime: Boolean = false
  def matchesEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, effectContext: EffectContext, subjectPhrase: IndefiniteNounPhrase[SubjectType]): Boolean
}

object IntransitiveEventMatchingVerb {
  case class WithSubject[SubjectType](subjectPhrase: IndefiniteNounPhrase[SubjectType], verb: IntransitiveEventMatchingVerb[SubjectType]) extends Condition {
    override def looksBackInTime: Boolean = verb.looksBackInTime
    override def matchesEvent(eventToMatch: HistoryEvent.ResolvedAction[_], gameState: GameState, effectContext: EffectContext): Boolean = {
      verb.matchesEvent(eventToMatch, gameState, effectContext, subjectPhrase)
    }
    override def getText(cardName: String): String = {
      subjectPhrase.getText(cardName) + " " + verb.inflect(VerbInflection.Present(subjectPhrase.person, subjectPhrase.number), cardName)
    }
  }
}
