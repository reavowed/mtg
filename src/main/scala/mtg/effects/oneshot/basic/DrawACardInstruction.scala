package mtg.effects.oneshot.basic

import mtg.effects.oneshot.InstructionResult
import mtg.effects.{Instruction, StackObjectResolutionContext}
import mtg.actions.DrawCardAction
import mtg.game.state.GameState

case object DrawACardInstruction extends Instruction {
  override def getText(cardName: String): String = "draw a card"
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (DrawCardAction(resolutionContext.controllingPlayer), resolutionContext)
  }
}
