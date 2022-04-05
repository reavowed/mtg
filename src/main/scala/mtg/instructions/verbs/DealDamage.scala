package mtg.instructions.verbs

import mtg.actions.damage.DealDamageAction
import mtg.core.{ObjectId, ObjectOrPlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, TransitiveInstructionVerb, Verb}

case class DealDamage(amount: Int) extends Verb.WithSuffix(Verb.Deal, s"$amount damage to") with TransitiveInstructionVerb[ObjectId, ObjectOrPlayerId] {
  override def resolve(sourceId: ObjectId, recipientId: ObjectOrPlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (DealDamageAction(sourceId, recipientId, amount), resolutionContext)
  }
}
