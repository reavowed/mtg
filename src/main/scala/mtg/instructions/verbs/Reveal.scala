package mtg.instructions.verbs

import mtg.actions.RevealAction
import mtg.definitions.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, MonotransitiveInstructionVerb, Verb}

case object Reveal extends Verb.RegularCaseObject with MonotransitiveInstructionVerb[PlayerId, ObjectId] {
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
    (RevealAction(playerId, objectId), resolutionContext)
  }
}
