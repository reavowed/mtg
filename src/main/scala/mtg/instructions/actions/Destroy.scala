package mtg.instructions.actions

import mtg.actions.DestroyAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, TransitiveVerbInstruction}
import mtg.text.Verb

case object Destroy extends Verb.RegularCaseObject with TransitiveVerbInstruction {
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (DestroyAction(objectId), resolutionContext)
  }
}
