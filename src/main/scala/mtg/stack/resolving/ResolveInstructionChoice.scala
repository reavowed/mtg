package mtg.stack.resolving

import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.{Choice, GameState, InternalGameAction}
import mtg.instructions.{Instruction, InstructionChoice, InstructionResult}

case class ResolveInstructionChoice(instructionChoice: InstructionChoice, remainingInstructions: Seq[Instruction], resolutionContext: StackObjectResolutionContext) extends Choice[InstructionResult] {
  override def playerToAct: PlayerId = instructionChoice.playerChoosing

  def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[InstructionResult] = {
    instructionChoice.parseDecision(serializedDecision, resolutionContext)
  }

  override def temporarilyVisibleZones: Seq[Zone] = instructionChoice.temporarilyVisibleZones

  override def temporarilyVisibleObjects: Seq[ObjectId] = instructionChoice.temporarilyVisibleObjects
}
