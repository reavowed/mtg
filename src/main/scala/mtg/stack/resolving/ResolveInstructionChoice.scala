package mtg.stack.resolving

import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.{Choice, GameState}
import mtg.instructions.{InstructionChoice, InstructionResult}

case class ResolveInstructionChoice(instructionChoice: InstructionChoice, resolutionContext: InstructionResolutionContext) extends Choice[InstructionResult] {
  override def playerToAct: PlayerId = instructionChoice.playerChoosing

  def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[InstructionResult] = {
    instructionChoice.parseDecision(serializedDecision, resolutionContext)
  }

  override def temporarilyVisibleZones: Seq[Zone] = instructionChoice.temporarilyVisibleZones

  override def temporarilyVisibleObjects: Seq[ObjectId] = instructionChoice.temporarilyVisibleObjects
}
