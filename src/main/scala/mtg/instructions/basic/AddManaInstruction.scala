package mtg.instructions.basic

import mtg.actions.AddManaAction
import mtg.core.symbols.ManaSymbol
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{Instruction, InstructionResult}

case class AddManaInstruction(symbols: ManaSymbol*) extends Instruction {
  override def getText(cardName: String): String = s"Add ${symbols.map(_.text).mkString}."

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (AddManaAction(resolutionContext.controllingPlayer, symbols), resolutionContext)
  }
}
