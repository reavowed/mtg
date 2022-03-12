package mtg.effects.oneshot.actions

import mtg.effects.oneshot.InstructionResult
import mtg.effects.{Instruction, StackObjectResolutionContext}
import mtg.actions.shuffle.ShuffleLibraryAction
import mtg.game.state.GameState

case object ShuffleInstruction extends Instruction {
  override def getText(cardName: String): String = "shuffle"

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (ShuffleLibraryAction(resolutionContext.controllingPlayer), resolutionContext)
  }
}
