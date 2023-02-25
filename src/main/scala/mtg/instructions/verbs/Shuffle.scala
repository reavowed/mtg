package mtg.instructions.verbs

import mtg.actions.shuffle.ShuffleLibraryAction
import mtg.definitions.PlayerId
import mtg.instructions.{InstructionAction, IntransitiveInstructionVerb, Verb}

case object Shuffle extends IntransitiveInstructionVerb[PlayerId] with Verb.RegularCaseObject {
  override def resolve(playerId: PlayerId): InstructionAction = {
    ShuffleLibraryAction(playerId)
  }
}
