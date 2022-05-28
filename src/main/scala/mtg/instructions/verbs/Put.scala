package mtg.instructions.verbs

import mtg.actions.PutCountersAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.{CountersLiteral, SingleIdentifyingNounPhrase}
import mtg.instructions.{InstructionResult, TransitiveInstructionVerb, Verb}
import mtg.parts.counters.CounterType

case class Put(countersPhrase: SingleIdentifyingNounPhrase[Map[CounterType, Int]])
    extends TransitiveInstructionVerb[PlayerId, ObjectId]
{
  override def inflect(verbInflection: VerbInflection, cardName: String): String = Verb.Put.inflect(verbInflection, cardName) + " " + countersPhrase.getText(cardName) + " on"
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (counters, resultingContext) = countersPhrase.identifySingle(gameState, resolutionContext)
    (PutCountersAction(counters, objectId), resultingContext)
  }
}

object Put {
  def apply(number: Int, kind: CounterType): Put = {
    Put(CountersLiteral(number, kind))
  }
}
