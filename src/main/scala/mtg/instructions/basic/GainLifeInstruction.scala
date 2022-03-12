package mtg.instructions.basic

import mtg.actions.GainLifeAction
import mtg.core.PlayerId
import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{Instruction, InstructionResult}

case class GainLifeInstruction(playerIdentifier: SingleIdentifier[PlayerId], amount: Int) extends Instruction {
  override def getText(cardName: String): String = s"you gain $amount life"

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (player, finalResolutionContext) = playerIdentifier.get(gameState, resolutionContext)
    (GainLifeAction(player, amount), finalResolutionContext)
  }
}
