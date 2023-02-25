package mtg.instructions.verbs

import mtg.actions.damage.DealDamageAction
import mtg.definitions.{ObjectId, ObjectOrPlayerId}
import mtg.instructions.{InstructionAction, MonotransitiveInstructionVerb, Verb}

case class DealDamage(amount: Int) extends Verb.WithSuffix(Verb.Deal, s"$amount damage to") with MonotransitiveInstructionVerb[ObjectId, ObjectOrPlayerId] {
  override def resolve(sourceId: ObjectId, recipientId: ObjectOrPlayerId): InstructionAction = {
    DealDamageAction(sourceId, recipientId, amount)
  }
}
