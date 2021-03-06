package mtg.instructions.verbs

import mtg.actions.GainLifeAction
import mtg.core.PlayerId
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, IntransitiveInstructionVerb, Verb}

case class GainLife(amount: Int) extends Verb.WithSuffix(Verb.Gain, s"$amount life") with IntransitiveInstructionVerb[PlayerId] {
  override def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
    (GainLifeAction(playerId, amount), resolutionContext)
  }
}
