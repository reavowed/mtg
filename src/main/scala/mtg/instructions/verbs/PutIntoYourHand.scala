package mtg.instructions.verbs

import mtg.actions.moveZone.MoveToHandAction
import mtg.definitions.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.{InstructionResult, MonotransitiveInstructionVerb, Verb}

case object PutIntoYourHand extends MonotransitiveInstructionVerb[PlayerId, ObjectId] {
  override def inflect(verbInflection: VerbInflection, cardName: String): String = Verb.Put.inflect(verbInflection, cardName)
  override def postObjectText: Option[String] = Some("into your hand")

  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
    (MoveToHandAction(objectId), resolutionContext)
  }
}
