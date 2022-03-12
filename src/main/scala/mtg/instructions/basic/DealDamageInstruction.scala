package mtg.instructions.basic

import mtg.core.{ObjectId, ObjectOrPlayerId}
import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{Instruction, InstructionResult}
import mtg.parts.damage.DealDamageEvent

case class DealDamageInstruction(sourceIdentifier: SingleIdentifier[ObjectId], recipientIdentifier: SingleIdentifier[ObjectOrPlayerId], amount: Int) extends Instruction {
  override def getText(cardName: String): String = sourceIdentifier.getText(cardName) + s" deals $amount damage to " + recipientIdentifier.getText(cardName)

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (source, contextAfterSource) = sourceIdentifier.get(gameState, resolutionContext)
    val (recipient, contextAfterRecipient) = recipientIdentifier.get(gameState, contextAfterSource)
    (DealDamageEvent(source, recipient, amount), contextAfterRecipient)
  }
}
