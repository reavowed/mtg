package mtg.instructions.verbs

import mtg.actions.moveZone.MoveToExileAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, TransitiveInstructionVerb}
import mtg.text.Verb

case object Exile extends Verb.RegularCaseObject with TransitiveInstructionVerb[PlayerId, ObjectId] {
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (MoveToExileAction(objectId), resolutionContext)
  }
}
