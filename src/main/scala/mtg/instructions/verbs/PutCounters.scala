package mtg.instructions.verbs

import mtg.actions.PutCountersAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.verbs.PutCounters.getCounterDescription
import mtg.instructions.{InstructionResult, TransitiveInstructionVerb, Verb}
import mtg.parts.counters.CounterType
import mtg.utils.TextUtils

case class PutCounters(number: Int, kind: CounterType)
    extends Verb.WithSuffix(Verb.Put, getCounterDescription(number, kind) + " on")
    with TransitiveInstructionVerb[PlayerId, ObjectId]
{
  override def resolve(playerId: PlayerId, objectId: ObjectId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (PutCountersAction(number, kind, objectId), resolutionContext)
  }
}

object PutCounters {
  def getCounterDescription(number: Int, kind: CounterType): String = {
    def counterDescription = kind.description
    def numberWord = TextUtils.getWord(number, counterDescription)
    def counterWord = if (number == 1) "counter" else "counters"
    Seq(numberWord, counterDescription, counterWord).mkString(" ")
  }
}
