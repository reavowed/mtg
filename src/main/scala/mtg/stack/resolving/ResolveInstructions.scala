package mtg.stack.resolving

import mtg.effects.StackObjectResolutionContext
import mtg.game.state._
import mtg.instructions.{Instruction, InstructionResult}

case class ResolveInstructions(allInstructions: Seq[Instruction], initialResolutionContext: StackObjectResolutionContext) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    executeInstructions(allInstructions, initialResolutionContext)
  }

  private def executeInstructions(
    instructions: Seq[Instruction],
    resolutionContext: StackObjectResolutionContext)(
    implicit gameState: GameState
  ): GameAction[Unit] = {
    instructions match {
      case instruction +: remainingInstructions =>
        ResolveNextInstruction(instruction, resolutionContext).flatMap(executeInstructions(remainingInstructions, _))
      case Nil =>
        ()
    }
  }
}

case class ResolveNextInstruction(instruction: Instruction, resolutionContext: StackObjectResolutionContext) extends DelegatingGameAction[StackObjectResolutionContext] {
  override def delegate(implicit gameState: GameState): GameAction[StackObjectResolutionContext] = {
    handleResult(instruction.resolve(gameState, resolutionContext))
  }

  private def handleResult(instructionResult: InstructionResult)(implicit gameState: GameState): GameAction[StackObjectResolutionContext] = {
    instructionResult match {
      case InstructionResult.Action(action, newResolutionContext) =>
        action.map(_ => newResolutionContext)
      case InstructionResult.Choice(choice) =>
        ResolveInstructionChoice(choice, resolutionContext).flatMap(handleResult)
      case InstructionResult.UpdatedContext(newResolutionContext) =>
        newResolutionContext
    }
  }
}
