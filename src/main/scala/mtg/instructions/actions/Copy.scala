package mtg.instructions.actions

import mtg.actions.CopySpellAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, TransitiveInstructionVerb}
import mtg.text.Verb

case object Copy extends Verb.RegularCaseObject with TransitiveInstructionVerb {
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (CopySpellAction(playerId, objectId), resolutionContext)
  }
}
