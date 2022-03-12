package mtg.instructions.actions

import mtg.effects.StackObjectResolutionContext
import mtg.actions.shuffle.ShuffleLibraryAction
import mtg.game.state.GameState
import mtg.instructions.{Instruction, InstructionResult}

case object ShuffleInstruction extends Instruction {
  override def getText(cardName: String): String = "shuffle"

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (ShuffleLibraryAction(resolutionContext.controllingPlayer), resolutionContext)
  }
}
