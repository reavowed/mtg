package mtg.stack.resolving

import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.{Choice, GameState, InternalGameAction}
import mtg.instructions.{Instruction, InstructionChoice}

case class ResolveInstructionChoice(instructionChoice: InstructionChoice, remainingInstructions: Seq[Instruction]) extends Choice[(Option[InternalGameAction], StackObjectResolutionContext)] {
  override def playerToAct: PlayerId = instructionChoice.playerChoosing

  def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[(Option[InternalGameAction], StackObjectResolutionContext)] = {
    instructionChoice.parseDecision(serializedDecision)
  }

  override def temporarilyVisibleZones: Seq[Zone] = instructionChoice.temporarilyVisibleZones

  override def temporarilyVisibleObjects: Seq[ObjectId] = instructionChoice.temporarilyVisibleObjects
}
