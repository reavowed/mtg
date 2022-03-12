package mtg.effects.oneshot.basic

import mtg.actions.AddManaAction
import mtg.core.symbols.ManaSymbol
import mtg.effects.oneshot.InstructionResult
import mtg.effects.{Instruction, StackObjectResolutionContext}
import mtg.game.state.GameState

case class AddManaInstruction(symbols: ManaSymbol*) extends Instruction {
  override def getText(cardName: String): String = s"Add ${symbols.map(_.text).mkString}."

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (AddManaAction(resolutionContext.controllingPlayer, symbols), resolutionContext)
  }
}
