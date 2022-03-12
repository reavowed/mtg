package mtg.effects.oneshot.basic

import mtg.core.ObjectId
import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.oneshot.InstructionResult
import mtg.effects.{Instruction, StackObjectResolutionContext}
import mtg.actions.moveZone.MoveToExileAction
import mtg.game.state.GameState

case class ExileInstruction(objectIdentifier: SingleIdentifier[ObjectId]) extends Instruction {
  override def getText(cardName: String): String = s"exile ${objectIdentifier.getText(cardName)}"

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (objectId, contextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    (MoveToExileAction(objectId), contextAfterObject)
  }
}
