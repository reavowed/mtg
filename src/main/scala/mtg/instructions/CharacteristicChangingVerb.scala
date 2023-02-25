package mtg.instructions

import mtg.actions.CreateContinousEffectsAction
import mtg.continuousEffects.CharacteristicOrControlChangingContinuousEffect
import mtg.definitions.ObjectId
import mtg.effects.condition.Condition
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.SetIdentifyingNounPhrase

trait CharacteristicChangingVerb extends Verb {
  def getEffects(objectId: ObjectId): Seq[CharacteristicOrControlChangingContinuousEffect]
}

object CharacteristicChangingVerb {
  case class WithSubjectAndCondition(subjectPhrase: SetIdentifyingNounPhrase[ObjectId], verb: CharacteristicChangingVerb, endCondition: Condition) extends Instruction {
    override def getText(cardName: String): String = {
      Seq(
        subjectPhrase.getText(cardName),
        verb.inflect(VerbInflection.Present(subjectPhrase), cardName),
        "until",
        endCondition.getText(cardName)
      ).mkString(" ")
    }
    override def resolve: InstructionAction = {
      subjectPhrase.identifyAll.flatMap { subjects =>
        val effects = subjects.flatMap(verb.getEffects)
        InstructionAction.withoutContextUpdate { (resolutionContext, _) =>
          CreateContinousEffectsAction(effects, resolutionContext, endCondition)
        }
      }
    }
  }
}
