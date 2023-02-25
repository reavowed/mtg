package mtg.instructions.verbs

import mtg.actions.GainLifeAction
import mtg.definitions.PlayerId
import mtg.instructions.{InstructionAction, IntransitiveInstructionVerb, Verb}

case class GainLife(amount: Int) extends Verb.WithSuffix(Verb.Gain, s"$amount life") with IntransitiveInstructionVerb[PlayerId] {
  override def resolve(playerId: PlayerId): InstructionAction = {
    GainLifeAction(playerId, amount)
  }
}
