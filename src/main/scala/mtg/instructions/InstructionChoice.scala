package mtg.instructions

import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.{GameState, GameObjectAction}

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
