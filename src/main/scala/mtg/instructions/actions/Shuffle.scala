package mtg.instructions.actions

import mtg.actions.shuffle.ShuffleLibraryAction
import mtg.core.PlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, IntransitiveInstructionVerb}
import mtg.text.Verb

case object Shuffle extends IntransitiveInstructionVerb[PlayerId] with Verb.RegularCaseObject {
  override def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (ShuffleLibraryAction(playerId), resolutionContext)
  }
}
