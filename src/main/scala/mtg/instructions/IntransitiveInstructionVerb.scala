package mtg.instructions

import mtg.definitions.PlayerId
import mtg.definitions.zones.ZoneType
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.{SingleIdentifyingNounPhrase, You}

trait IntransitiveInstructionVerb[-SubjectType] extends Verb {
  def resolve(subject: SubjectType, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult
  def getFunctionalZones(subjectPhrase: SingleIdentifyingNounPhrase[SubjectType]): Option[Set[ZoneType]] = None
}

object IntransitiveInstructionVerb {
  implicit def toInstruction(intransitiveVerbInstruction: IntransitiveInstructionVerb[PlayerId]): Instruction = {
    Imperative(intransitiveVerbInstruction)
  }

  case class Imperative(instructionVerb: IntransitiveInstructionVerb[PlayerId]) extends Instruction {
    override def getText(cardName: String): String = instructionVerb.inflect(VerbInflection.Imperative, cardName)
    override def resolve(gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
      instructionVerb.resolve(resolutionContext.youPlayerId, gameState, resolutionContext)
    }
    override def functionalZones: Option[Set[ZoneType]] = {
      instructionVerb.getFunctionalZones(You)
    }
  }
  case class WithSubject[SubjectType](subjectPhrase: SingleIdentifyingNounPhrase[SubjectType], instructionVerb: IntransitiveInstructionVerb[SubjectType]) extends Instruction {
    override def getText(cardName: String): String = {
      subjectPhrase.getText(cardName) + " " + instructionVerb.inflect(VerbInflection.Present(subjectPhrase), cardName)
    }
    override def resolve(gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
      val (playerId, contextAfterPlayer) = subjectPhrase.identifySingle(gameState, resolutionContext)
      instructionVerb.resolve(playerId, gameState, contextAfterPlayer)
    }
    override def functionalZones: Option[Set[ZoneType]] = {
      instructionVerb.getFunctionalZones(subjectPhrase)
    }
  }
  case class WithKnownSubject[SubjectType](subject: SubjectType, instructionVerb: IntransitiveInstructionVerb[SubjectType]) extends ResolvableInstructionPart {
    override def resolve(gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
      instructionVerb.resolve(subject, gameState, resolutionContext)
    }
  }
}
