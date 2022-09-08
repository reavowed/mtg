package mtg.instructions

import mtg.definitions.zones.Zone
import mtg.definitions.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState

abstract class InstructionChoice {
  def playerChoosing: PlayerId
  def parseDecision(
    serializedDecision: String,
    resolutionContext: InstructionResolutionContext)(
    implicit gameState: GameState
  ): Option[InstructionResult]
  def temporarilyVisibleZones: Seq[Zone] = Nil
  def temporarilyVisibleObjects: Seq[ObjectId] = Nil
}
