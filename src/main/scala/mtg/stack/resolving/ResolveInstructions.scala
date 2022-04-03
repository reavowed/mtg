package mtg.stack.resolving

import mtg.effects.StackObjectResolutionContext
import mtg.game.state._
import mtg.instructions.{Instruction, InstructionResult}

case class ResolveInstructions(allInstructions: Seq[Instruction], initialResolutionContext: StackObjectResolutionContext) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    executeInstructions(allInstructions, initialResolutionContext)
  }

  private def executeInstructions(
    instructions: Seq[Instruction],
    resolutionContext: StackObjectResolutionContext)(
    implicit gameState: GameState
  ): PartialGameActionResult[Unit] = {
    instructions match {
      case instruction +: remainingInstructions =>
        handeInstructionResult(
          instruction.resolve(gameState, resolutionContext),
          remainingInstructions,
          resolutionContext)
      case Nil =>
        ()
    }
  }

  private def handeInstructionResult(
    instructionResult: InstructionResult,
    remainingInstructions: Seq[Instruction],
    resolutionContext: StackObjectResolutionContext)(
    implicit gameState: GameState
  ): PartialGameActionResult[Unit] = {
    instructionResult match {
      case InstructionResult.Event(event, newResolutionContext) =>
        PartialGameActionResult.ChildWithCallback(
          WrappedOldUpdates(event),
          (_: Unit, gameState) => executeInstructions(remainingInstructions, newResolutionContext)(gameState))
      case InstructionResult.Choice(choice) =>
        PartialGameActionResult.ChildWithCallback(
          ResolveInstructionChoice(choice, remainingInstructions, resolutionContext),
          (instructionResult: InstructionResult, gameState) => handeInstructionResult(instructionResult, remainingInstructions, resolutionContext)(gameState))
      case InstructionResult.Log(logEvent, newResolutionContext) =>
        PartialGameActionResult.ChildWithCallback(
          logEvent,
          (_: Unit, gameState) => executeInstructions(remainingInstructions, newResolutionContext)(gameState))
      case InstructionResult.UpdatedContext(newResolutionContext) =>
        executeInstructions(remainingInstructions, newResolutionContext)
    }
  }
}
