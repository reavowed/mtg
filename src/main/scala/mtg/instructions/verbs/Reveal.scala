package mtg.instructions.verbs

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.history.LogEvent
import mtg.game.state.{CurrentCharacteristics, GameState}
import mtg.instructions.{InstructionResult, TransitiveInstructionVerb}
import mtg.text.Verb

case object Reveal extends Verb.RegularCaseObject with TransitiveInstructionVerb[PlayerId, ObjectId] {
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (LogEvent.RevealCard(resolutionContext.controllingPlayer, CurrentCharacteristics.getName(objectId, gameState)), resolutionContext)
  }
}
