package mtg.instructions

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.effects.identifiers.SingleIdentifier
import mtg.game.state.GameState
import mtg.text.{Verb, VerbInflection}

trait TransitiveVerbInstruction extends Verb {
  def apply(objectIdentifier: SingleIdentifier[ObjectId]): IntransitiveVerbInstruction = {
    TransitiveVerbInstructionWithObject(this, objectIdentifier)
  }
  def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult
}

case class TransitiveVerbInstructionWithObject(
    transitiveVerbInstruction: TransitiveVerbInstruction,
    objectIdentifier: SingleIdentifier[ObjectId])
  extends IntransitiveVerbInstruction
{
  override def inflect(verbInflection: VerbInflection, cardName: String): String = transitiveVerbInstruction.inflect(verbInflection, cardName) + " " + objectIdentifier.getText(cardName)
  override def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (objectId, contextAfterObjects) = objectIdentifier.get(gameState, resolutionContext)
    transitiveVerbInstruction.resolve(playerId, objectId, gameState, contextAfterObjects)
  }
}
