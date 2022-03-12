package mtg.instructions.basic

import mtg.core.ObjectId
import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.StackObjectResolutionContext
import mtg.actions.moveZone.MoveToHandAction
import mtg.game.state.GameState
import mtg.instructions.{Instruction, InstructionResult}

case class PutIntoHandInstruction(objectIdentifier: SingleIdentifier[ObjectId]) extends Instruction {
  override def getText(cardName: String): String = s"put ${objectIdentifier.getText(cardName)} into your hand"

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (objectId, contextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    (MoveToHandAction(objectId), contextAfterObject)
  }
}
