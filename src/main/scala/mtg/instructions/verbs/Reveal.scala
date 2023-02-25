package mtg.instructions.verbs

import mtg.actions.RevealAction
import mtg.definitions.{ObjectId, PlayerId}
import mtg.instructions.{InstructionAction, MonotransitiveInstructionVerb, Verb}

case object Reveal extends Verb.RegularCaseObject with MonotransitiveInstructionVerb[PlayerId, ObjectId] {
  override def resolve(playerId: PlayerId, objectId: ObjectId): InstructionAction = {
    RevealAction(playerId, objectId)
  }
}
