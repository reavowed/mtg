package mtg.instructions.verbs

import mtg.actions.DrawCardAction
import mtg.core.PlayerId
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, IntransitiveInstructionVerb, Verb}

case object DrawACard extends Verb.WithSuffix(Verb.Draw, "a card") with IntransitiveInstructionVerb[PlayerId] {
  override def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
    (DrawCardAction(playerId), resolutionContext)
  }
}
