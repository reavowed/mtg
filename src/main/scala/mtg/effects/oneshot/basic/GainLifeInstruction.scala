package mtg.effects.oneshot.basic

import mtg.actions.GainLifeAction
import mtg.core.PlayerId
import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.oneshot.InstructionResult
import mtg.effects.{Instruction, StackObjectResolutionContext}
import mtg.game.state.GameState

case class GainLifeInstruction(playerIdentifier: SingleIdentifier[PlayerId], amount: Int) extends Instruction {
  override def getText(cardName: String): String = s"you gain $amount life"

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (player, finalResolutionContext) = playerIdentifier.get(gameState, resolutionContext)
    (GainLifeAction(player, amount), finalResolutionContext)
  }
}
