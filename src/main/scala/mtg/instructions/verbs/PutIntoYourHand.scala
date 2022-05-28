package mtg.instructions.verbs

import mtg.actions.moveZone.MoveToHandAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.{InstructionResult, TransitiveInstructionVerb, Verb}

case object PutIntoYourHand extends TransitiveInstructionVerb[PlayerId, ObjectId] {
  override def inflect(verbInflection: VerbInflection, cardName: String): String = Verb.Put.inflect(verbInflection, cardName)
  override def postObjectText: Option[String] = Some("into your hand")

  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (MoveToHandAction(objectId), resolutionContext)
  }
}
