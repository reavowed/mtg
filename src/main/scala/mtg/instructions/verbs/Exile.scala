package mtg.instructions.verbs

import mtg.actions.moveZone.MoveToExileAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, MonotransitiveInstructionVerb, Verb}

case object Exile extends Verb.RegularCaseObject with MonotransitiveInstructionVerb[PlayerId, ObjectId] {
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
    (MoveToExileAction(objectId), resolutionContext)
  }
}
