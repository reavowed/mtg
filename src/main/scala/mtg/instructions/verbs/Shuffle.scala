package mtg.instructions.verbs

import mtg.actions.shuffle.ShuffleLibraryAction
import mtg.core.PlayerId
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, IntransitiveInstructionVerb, Verb}

case object Shuffle extends IntransitiveInstructionVerb[PlayerId] with Verb.RegularCaseObject {
  override def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
    (ShuffleLibraryAction(playerId), resolutionContext)
  }
}
