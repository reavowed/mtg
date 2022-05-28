package mtg.instructions

import mtg.abilities.StaticAbilityParagraph
import mtg.continuousEffects.ContinuousEffect
import mtg.effects.EffectContext
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.StaticSingleIdentifyingNounPhrase

trait IntransitiveStaticAbilityVerb[-SubjectType] extends Verb {
  def getEffects(subjectPhrase: StaticSingleIdentifyingNounPhrase[SubjectType], effectContext: EffectContext): Seq[ContinuousEffect]
}

object IntransitiveStaticAbilityVerb {
  case class WithSubject[SubjectType](
    subjectPhrase: StaticSingleIdentifyingNounPhrase[SubjectType],
    verb: IntransitiveStaticAbilityVerb[SubjectType]
  ) extends StaticAbilityParagraph {
    override def getText(cardName: String): String = {
      subjectPhrase.getText(cardName) +
        " " +
        verb.inflect(VerbInflection.Present(subjectPhrase), cardName)
    }

    override def getEffects(effectContext: EffectContext): Seq[ContinuousEffect] = {
      verb.getEffects(subjectPhrase, effectContext)
    }
  }
}


