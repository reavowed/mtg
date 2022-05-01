package mtg.instructions.verbs

import mtg.actions.PutCountersAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, TransitiveInstructionVerb, Verb}
import mtg.parts.counters.{CounterSpecification, CounterType}

case class PutCounters(counterSpecification: CounterSpecification)
    extends Verb.WithSuffix(Verb.Put, counterSpecification.description + " on")
    with TransitiveInstructionVerb[PlayerId, ObjectId]
{
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (PutCountersAction(counterSpecification, objectId), resolutionContext)
  }
}

object PutCounters {
  def apply(number: Int, kind: CounterType): PutCounters = {
    PutCounters(CounterSpecification(number, kind))
  }
}
