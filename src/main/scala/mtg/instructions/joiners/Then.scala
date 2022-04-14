package mtg.instructions.joiners

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{Instruction, InstructionResult}
import mtg.utils.TextUtils._

object Then {
  def apply(instructions: Instruction*): Instruction = new Instruction {
    override def getText(cardName: String): String = instructions.map(_.getText(cardName)).toCommaList("then")
    override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = (instructions, resolutionContext)
  }
}
