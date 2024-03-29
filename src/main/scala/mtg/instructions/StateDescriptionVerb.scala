package mtg.instructions

import mtg.definitions.ObjectId
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.StaticSingleIdentifyingNounPhrase

trait StateDescriptionVerb[-SubjectType] extends Verb {
  def describes(subject: SubjectType, objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean
}

object StateDescriptionVerb {
  case class WithSubject[SubjectType](
      subjectPhrase: StaticSingleIdentifyingNounPhrase[SubjectType],
      verb: StateDescriptionVerb[SubjectType])
    extends SuffixDescriptor
  {
    override def getText(cardName: String): String = subjectPhrase.getText(cardName) + " " + verb.inflect(VerbInflection.Present(subjectPhrase), cardName)
    override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
      val subject = subjectPhrase.identify(effectContext)
      verb.describes(subject, objectId, gameState, effectContext)
    }
  }
}
