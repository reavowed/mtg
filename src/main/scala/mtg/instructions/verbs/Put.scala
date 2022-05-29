package mtg.instructions.verbs

import mtg.actions.PutCountersAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.{CountersLiteral, SingleIdentifyingNounPhrase}
import mtg.instructions.{InstructionResult, TransitiveInstructionVerb, Verb}
import mtg.parts.Counter

case class Put(countersPhrase: SingleIdentifyingNounPhrase[Map[Counter, Int]])
    extends TransitiveInstructionVerb[PlayerId, ObjectId]
{
  override def inflect(verbInflection: VerbInflection, cardName: String): String = Verb.Put.inflect(verbInflection, cardName) + " " + countersPhrase.getText(cardName) + " on"
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
    val (counters, resultingContext) = countersPhrase.identifySingle(gameState, resolutionContext)
    (PutCountersAction(counters, objectId), resultingContext)
  }
}

object Put {
  def apply(number: Int, kind: Counter): Put = {
    Put(CountersLiteral(number, kind))
  }
}
