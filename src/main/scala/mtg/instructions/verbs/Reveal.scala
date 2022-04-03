package mtg.instructions.verbs

import mtg.actions.RevealAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, TransitiveInstructionVerb, Verb}

case object Reveal extends Verb.RegularCaseObject with TransitiveInstructionVerb[PlayerId, ObjectId] {
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (RevealAction(playerId, objectId), resolutionContext)
  }
}
