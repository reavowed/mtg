package mtg.stack.resolving

import mtg.effects.oneshot.InstructionResult
import mtg.effects.{Instruction, StackObjectResolutionContext}
import mtg.game.state._

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
        instruction.resolve(gameState, resolutionContext) match {
          case InstructionResult.Event(event, newResolutionContext) =>
            PartialGameActionResult.ChildWithCallback(
              WrappedOldUpdates(event),
              (_: Unit, gameState) => executeInstructions(remainingInstructions, newResolutionContext)(gameState))
          case InstructionResult.Choice(choice) =>
            PartialGameActionResult.ChildWithCallback(
              ResolveInstructionChoice(choice, remainingInstructions),
              handleDecision(remainingInstructions))
          case InstructionResult.Log(logEvent, newResolutionContext) =>
            PartialGameActionResult.ChildWithCallback(
              logEvent,
              (_: Unit, gameState) => executeInstructions(remainingInstructions, newResolutionContext)(gameState))
        }
      case Nil =>
        ()
    }
  }

  private def handleDecision(
    remainingInstructions: Seq[Instruction])(
    decision: (Option[InternalGameAction], StackObjectResolutionContext),
    gameState: GameState
  ): PartialGameActionResult[Unit] = decision match {
    case (Some(action), newResolutionContext) =>
      PartialGameActionResult.ChildWithCallback(
        WrappedOldUpdates(action),
        (_: Unit, gameState) => executeInstructions(remainingInstructions, newResolutionContext)(gameState))
    case (None, newResolutionContext) =>
      executeInstructions(remainingInstructions, newResolutionContext)(gameState)
  }
}
