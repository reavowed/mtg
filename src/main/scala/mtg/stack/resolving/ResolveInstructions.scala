package mtg.stack.resolving

import mtg.effects.InstructionResolutionContext
import mtg.game.state._
import mtg.instructions.ResolvableInstructionPart

case class ResolveInstructions(allInstructions: Seq[ResolvableInstructionPart], initialResolutionContext: InstructionResolutionContext) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    ResolveInstructions.executeInstructions(allInstructions, initialResolutionContext)
  }
}

object ResolveInstructions {
  def executeInstructions(
    instructions: Seq[ResolvableInstructionPart],
    resolutionContext: InstructionResolutionContext
  ): GameAction[InstructionResolutionContext] = {
    instructions match {
      case instruction +: remainingInstructions =>
        instruction.resolve(resolutionContext).flatMap(executeInstructions(remainingInstructions, _))
      case Nil =>
        resolutionContext
    }
  }
}
