package mtg.instructions.basic

import mtg.effects.StackObjectResolutionContext
import mtg.actions.DrawCardAction
import mtg.game.state.GameState
import mtg.instructions.{Instruction, InstructionResult}

case object DrawACardInstruction extends Instruction {
  override def getText(cardName: String): String = "draw a card"
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (DrawCardAction(resolutionContext.controllingPlayer), resolutionContext)
  }
}
