package mtg.instructions

import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameAction

sealed trait InstructionResult
object InstructionResult {
  case class NewInstructions(instructions: Seq[ResolvableInstructionPart], resolutionContext: InstructionResolutionContext) extends InstructionResult
  case class Action(gameAction: GameAction[_], resolutionContext: InstructionResolutionContext) extends InstructionResult
  case class Choice(instructionChoice: InstructionChoice) extends InstructionResult
  case class UpdatedContext(resolutionContext: InstructionResolutionContext) extends InstructionResult

  implicit def instructionToInstructionResult(tuple: (ResolvableInstructionPart, InstructionResolutionContext)): InstructionResult = NewInstructions(Seq(tuple._1), tuple._2)
  implicit def instructionsToInstructionResult(tuple: (Seq[ResolvableInstructionPart], InstructionResolutionContext)): InstructionResult = NewInstructions(tuple._1, tuple._2)
  implicit def actionToInstructionResult(tuple: (GameAction[_], InstructionResolutionContext)): InstructionResult = Action(tuple._1, tuple._2)
  implicit def choiceToInstructionResult(choice: InstructionChoice): InstructionResult = Choice(choice)
  implicit def contextToInstructionResult(resolutionContext: InstructionResolutionContext): InstructionResult = UpdatedContext(resolutionContext)
}
