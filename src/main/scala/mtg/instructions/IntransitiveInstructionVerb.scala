package mtg.instructions

import mtg.definitions.PlayerId
import mtg.definitions.zones.ZoneType
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.{SingleIdentifyingNounPhrase, You}

trait IntransitiveInstructionVerb[-SubjectType] extends Verb {
  def resolve(subject: SubjectType): InstructionAction
  def getFunctionalZones(subjectPhrase: SingleIdentifyingNounPhrase[SubjectType]): Option[Set[ZoneType]] = None
}

object IntransitiveInstructionVerb {
  implicit def toInstruction(intransitiveVerbInstruction: IntransitiveInstructionVerb[PlayerId]): Instruction = {
    Imperative(intransitiveVerbInstruction)
  }

  case class Imperative(instructionVerb: IntransitiveInstructionVerb[PlayerId]) extends Instruction {
    override def getText(cardName: String): String = instructionVerb.inflect(VerbInflection.Imperative, cardName)
    override def resolve: InstructionAction = InstructionAction.delegating { resolutionContext =>
      instructionVerb.resolve(resolutionContext.youPlayerId)
    }
    override def functionalZones: Option[Set[ZoneType]] = {
      instructionVerb.getFunctionalZones(You)
    }
  }
  case class WithSubject[SubjectType](subjectPhrase: SingleIdentifyingNounPhrase[SubjectType], instructionVerb: IntransitiveInstructionVerb[SubjectType]) extends Instruction {
    override def getText(cardName: String): String = {
      subjectPhrase.getText(cardName) + " " + instructionVerb.inflect(VerbInflection.Present(subjectPhrase), cardName)
    }
    override def resolve: InstructionAction = {
      subjectPhrase.identifySingle.flatMap(instructionVerb.resolve)
    }
    override def functionalZones: Option[Set[ZoneType]] = {
      instructionVerb.getFunctionalZones(subjectPhrase)
    }
  }
  case class WithKnownSubject[SubjectType](subject: SubjectType, instructionVerb: IntransitiveInstructionVerb[SubjectType]) extends ResolvableInstructionPart {
    override def resolve: InstructionAction = {
      instructionVerb.resolve(subject)
    }
  }
}
