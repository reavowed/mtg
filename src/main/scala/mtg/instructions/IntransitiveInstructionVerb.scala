package mtg.instructions

import mtg.core.PlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.effects.identifiers.SingleIdentifier
import mtg.game.state.GameState
import mtg.text.{Verb, VerbInflection}

trait IntransitiveInstructionVerb extends Verb {
  def baseVerb: Verb = this
  def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult

  def imperative: Instruction = IntransitiveInstructionVerb.Imperative(this)
  def apply(playerIdentifier: SingleIdentifier[PlayerId]): Instruction = {
    IntransitiveInstructionVerb.WithPlayer(playerIdentifier, this)
  }
}

object IntransitiveInstructionVerb {
  implicit def toInstruction(intransitiveVerbInstruction: IntransitiveInstructionVerb): Instruction = {
    intransitiveVerbInstruction.imperative
  }

  case class Imperative(instructionVerb: IntransitiveInstructionVerb) extends Instruction {
    override def getText(cardName: String): String = instructionVerb.inflect(VerbInflection.Imperative, cardName)
    override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
      instructionVerb.resolve(resolutionContext.controllingPlayer, gameState, resolutionContext)
    }
  }
  case class WithPlayer(playerIdentifier: SingleIdentifier[PlayerId], instructionVerb: IntransitiveInstructionVerb) extends Instruction {
    override def getText(cardName: String): String = {
      playerIdentifier.getText(cardName) + " " + instructionVerb.inflect(VerbInflection.Present(playerIdentifier.person, playerIdentifier.number), cardName)
    }
    override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
      val (playerId, contextAfterPlayer) = playerIdentifier.get(gameState, resolutionContext)
      instructionVerb.resolve(playerId, gameState, contextAfterPlayer)
    }
  }
}
