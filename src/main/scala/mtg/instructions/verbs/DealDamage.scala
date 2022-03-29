package mtg.instructions.verbs

import mtg.core.{ObjectId, ObjectOrPlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, TransitiveInstructionVerb}
import mtg.parts.damage.DealDamageEvent
import mtg.text.Verb

case class DealDamage(amount: Int) extends Verb.WithSuffix(Verb.Deal, s"$amount damage to") with TransitiveInstructionVerb[ObjectId, ObjectOrPlayerId] {
  override def resolve(sourceId: ObjectId, recipientId: ObjectOrPlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (DealDamageEvent(sourceId, recipientId, amount), resolutionContext)
  }
}
