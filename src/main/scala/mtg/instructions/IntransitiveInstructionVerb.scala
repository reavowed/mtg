package mtg.instructions

import mtg.core.PlayerId
import mtg.core.zones.ZoneType
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.nounPhrases.{SingleIdentifyingNounPhrase, You}

trait IntransitiveInstructionVerb[-SubjectType] extends Verb {
  def resolve(subject: SubjectType, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult
  def getFunctionalZones(subjectPhrase: SingleIdentifyingNounPhrase[SubjectType]): Option[Set[ZoneType]] = None
}

object IntransitiveInstructionVerb {
  implicit def toInstruction(intransitiveVerbInstruction: IntransitiveInstructionVerb[PlayerId]): Instruction = {
    Imperative(intransitiveVerbInstruction)
  }

  case class Imperative(instructionVerb: IntransitiveInstructionVerb[PlayerId]) extends Instruction {
    override def getText(cardName: String): String = instructionVerb.inflect(VerbInflection.Imperative, cardName)
    override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
      instructionVerb.resolve(resolutionContext.controllingPlayer, gameState, resolutionContext)
    }
    override def functionalZones: Option[Set[ZoneType]] = {
      instructionVerb.getFunctionalZones(You)
    }
  }
  case class WithSubject[SubjectType](subjectPhrase: SingleIdentifyingNounPhrase[SubjectType], instructionVerb: IntransitiveInstructionVerb[SubjectType]) extends Instruction {
    override def getText(cardName: String): String = {
      subjectPhrase.getText(cardName) + " " + instructionVerb.inflect(VerbInflection.Present(subjectPhrase.person, subjectPhrase.number), cardName)
    }
    override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
      val (playerId, contextAfterPlayer) = subjectPhrase.identifySingle(gameState, resolutionContext)
      instructionVerb.resolve(playerId, gameState, contextAfterPlayer)
    }
    override def functionalZones: Option[Set[ZoneType]] = {
      instructionVerb.getFunctionalZones(subjectPhrase)
    }
  }
  case class WithKnownSubject[SubjectType](subject: SubjectType, instructionVerb: IntransitiveInstructionVerb[SubjectType]) extends ResolvableInstructionPart {
    override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
      instructionVerb.resolve(subject, gameState, resolutionContext)
    }
  }
}
