package mtg.instructions.verbs

import mtg.actions.moveZone.MoveToExileAction
import mtg.definitions.{ObjectId, PlayerId}
import mtg.instructions.{InstructionAction, MonotransitiveInstructionVerb, Verb}

case object Exile extends Verb.RegularCaseObject with MonotransitiveInstructionVerb[PlayerId, ObjectId] {
  override def resolve(playerId: PlayerId, objectId: ObjectId): InstructionAction = {
    MoveToExileAction(objectId)
  }
}
