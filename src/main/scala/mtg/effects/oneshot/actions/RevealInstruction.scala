package mtg.effects.oneshot.actions

import mtg.core.ObjectId
import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.oneshot.InstructionResult
import mtg.effects.{Instruction, StackObjectResolutionContext}
import mtg.game.state.{CurrentCharacteristics, GameState}
import mtg.game.state.history.LogEvent

case class RevealInstruction(objectIdentifier: SingleIdentifier[ObjectId]) extends Instruction {
  override def getText(cardName: String): String = "reveal " + objectIdentifier.getText(cardName)

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (objectId, contextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    (LogEvent.RevealCard(resolutionContext.controllingPlayer, CurrentCharacteristics.getName(objectId, gameState)), contextAfterObject)
  }
}
