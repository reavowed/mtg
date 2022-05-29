package mtg.stack.resolving

import mtg.effects.InstructionResolutionContext
import mtg.game.state._
import mtg.instructions.{Instruction, InstructionResult, ResolvableInstructionPart}

case class ResolveInstructions(allInstructions: Seq[ResolvableInstructionPart], initialResolutionContext: InstructionResolutionContext) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    executeInstructions(allInstructions, initialResolutionContext)
  }

  private def executeInstructions(
    instructions: Seq[ResolvableInstructionPart],
    resolutionContext: InstructionResolutionContext
  ): GameAction[InstructionResolutionContext] = {
    instructions match {
      case instruction +: remainingInstructions =>
        resolveInstruction(instruction, resolutionContext).flatMap(executeInstructions(remainingInstructions, _))
      case Nil =>
        resolutionContext
    }
  }

  private def resolveInstruction(instruction: ResolvableInstructionPart, resolutionContext: InstructionResolutionContext): GameAction[InstructionResolutionContext] = { (gameState: GameState) =>
    handleResult(instruction.resolve(gameState, resolutionContext), resolutionContext)
  }

  private def handleResult(instructionResult: InstructionResult, resolutionContext: InstructionResolutionContext): GameAction[InstructionResolutionContext] = { (gameState: GameState) =>
    instructionResult match {
      case InstructionResult.NewInstructions(instructions, newResolutionContext) =>
        executeInstructions(instructions, newResolutionContext)
      case InstructionResult.Action(action, newResolutionContext) =>
        action.map(_ => newResolutionContext)
      case InstructionResult.Choice(choice) =>
        ResolveInstructionChoice(choice, resolutionContext).flatMap(handleResult(_, resolutionContext))
      case InstructionResult.UpdatedContext(newResolutionContext) =>
        GameAction.constant(newResolutionContext)
    }
  }
}
