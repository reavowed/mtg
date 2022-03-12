package mtg.instructions.basic

import mtg.core.PlayerId
import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.StackObjectResolutionContext
import mtg.actions.DrawCardAction
import mtg.game.state.GameState
import mtg.instructions.{Instruction, InstructionResult}

case class DrawsACardInstruction(playerIdentifier: SingleIdentifier[PlayerId]) extends Instruction {
  override def getText(cardName: String): String = playerIdentifier.getText(cardName) + " draws a card"
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    playerIdentifier.get(gameState, resolutionContext).mapLeft(DrawCardAction)
  }
}
