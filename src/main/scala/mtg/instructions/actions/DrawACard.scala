package mtg.instructions.actions

import mtg.actions.DrawCardAction
import mtg.core.PlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, IntransitiveInstructionVerb}
import mtg.text.Verb

case object DrawACard extends Verb.WithSuffix(Verb.Draw, "a card") with IntransitiveInstructionVerb[PlayerId] {
  override def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (DrawCardAction(playerId), resolutionContext)
  }
}
