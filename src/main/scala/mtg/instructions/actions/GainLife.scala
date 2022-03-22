package mtg.instructions.actions

import mtg.actions.GainLifeAction
import mtg.core.PlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, IntransitiveInstructionVerb}
import mtg.text.Verb

case class GainLife(amount: Int) extends Verb.WithSuffix(Verb.Gain, s"$amount life") with IntransitiveInstructionVerb[PlayerId] {
  override def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (GainLifeAction(playerId, amount), resolutionContext)
  }
}
