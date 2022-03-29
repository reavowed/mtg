package mtg.instructions

import mtg.actions.CreateContinousEffectsAction
import mtg.continuousEffects.CharacteristicOrControlChangingContinuousEffect
import mtg.core.ObjectId
import mtg.effects.StackObjectResolutionContext
import mtg.effects.condition.Condition
import mtg.game.state.GameState
import mtg.instructions.nounPhrases.SetIdentifyingNounPhrase
import mtg.text.{Verb, VerbInflection}

trait CharacteristicChangingVerb extends Verb {
  def getEffects(objectId: ObjectId): Seq[CharacteristicOrControlChangingContinuousEffect]
}

object CharacteristicChangingVerb {
  case class WithSubjectAndCondition(subjectPhrase: SetIdentifyingNounPhrase[ObjectId], verb: CharacteristicChangingVerb, endCondition: Condition) extends Instruction {
    override def getText(cardName: String): String = {
      Seq(
        subjectPhrase.getText(cardName),
        verb.inflect(VerbInflection.Present(subjectPhrase.person, subjectPhrase.number), cardName),
        "until",
        endCondition.getText(cardName)
      ).mkString(" ")
    }
    override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
      val (subjects, contextAfterSubject) = subjectPhrase.identifyAll(gameState, resolutionContext)
      val effects = subjects.flatMap(verb.getEffects)
      (CreateContinousEffectsAction(effects, contextAfterSubject, endCondition), contextAfterSubject)
    }
  }
}
