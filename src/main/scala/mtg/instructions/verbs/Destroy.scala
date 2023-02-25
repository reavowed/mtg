package mtg.instructions.verbs

import mtg.actions.DestroyAction
import mtg.definitions.{ObjectId, PlayerId}
import mtg.instructions.{InstructionAction, MonotransitiveInstructionVerb, Verb}

case object Destroy extends Verb.RegularCaseObject with MonotransitiveInstructionVerb[PlayerId, ObjectId] {
  override def resolve(playerId: PlayerId, objectId: ObjectId): InstructionAction = {
    DestroyAction(objectId)
  }
}
