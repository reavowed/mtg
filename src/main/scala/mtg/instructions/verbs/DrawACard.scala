package mtg.instructions.verbs

import mtg.actions.DrawCardAction
import mtg.definitions.PlayerId
import mtg.instructions.{InstructionAction, IntransitiveInstructionVerb, Verb}

case object DrawACard extends Verb.WithSuffix(Verb.Draw, "a card") with IntransitiveInstructionVerb[PlayerId] {
  override def resolve(playerId: PlayerId): InstructionAction = {
    DrawCardAction(playerId)
  }
}
