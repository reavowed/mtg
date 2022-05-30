package mtg.instructions.verbs

import mtg.actions.PutCountersAction
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.{CountersLiteral, SingleIdentifyingNounPhrase}
import mtg.instructions.{BitransitiveInstructionVerb, InstructionResult, IntransitiveInstructionVerb, Verb}
import mtg.parts.Counter

case object Put extends BitransitiveInstructionVerb[PlayerId, Map[Counter, Int], ObjectId] with Verb.RegularCaseObject
{
  override def objectJoiningText: String = "on"
  override def resolve(playerId: PlayerId, counters: Map[Counter, Int], objectId: ObjectId, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
    (PutCountersAction(counters, objectId), resolutionContext)
  }
  def apply(number: Int, kind: Counter, objectPhrase: SingleIdentifyingNounPhrase[ObjectId]): IntransitiveInstructionVerb[PlayerId] = {
    Put(CountersLiteral(number, kind), objectPhrase)
  }
}
