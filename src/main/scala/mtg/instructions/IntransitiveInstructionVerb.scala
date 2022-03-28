package mtg.instructions

import mtg.core.PlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.effects.identifiers.SingleIdentifier
import mtg.game.state.GameState
import mtg.instructions.nouns.SingleIdentifyingNounPhrase
import mtg.text.{Verb, VerbInflection}

trait IntransitiveInstructionVerb[SubjectType] extends Verb {
  def resolve(subject: SubjectType, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult
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
  }
  case class WithSubject[SubjectType](subjectNoun: SingleIdentifyingNounPhrase[SubjectType], instructionVerb: IntransitiveInstructionVerb[SubjectType]) extends Instruction {
    override def getText(cardName: String): String = {
      subjectNoun.getText(cardName) + " " + instructionVerb.inflect(VerbInflection.Present(subjectNoun.person, subjectNoun.number), cardName)
    }
    override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
      val (playerId, contextAfterPlayer) = subjectNoun.identifySingle(gameState, resolutionContext)
      instructionVerb.resolve(playerId, gameState, contextAfterPlayer)
    }
  }
}
