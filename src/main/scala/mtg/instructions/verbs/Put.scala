package mtg.instructions.verbs

import mtg.actions.PutCountersAction
import mtg.definitions.{ObjectId, PlayerId}
import mtg.instructions.nounPhrases.{CountersLiteral, SingleIdentifyingNounPhrase}
import mtg.instructions.{BitransitiveInstructionVerb, InstructionAction, IntransitiveInstructionVerb, Verb}
import mtg.parts.Counter

case object Put extends BitransitiveInstructionVerb[PlayerId, Map[Counter, Int], ObjectId] with Verb.RegularCaseObject
{
  override def objectJoiningText: String = "on"
  override def resolve(playerId: PlayerId, counters: Map[Counter, Int], objectId: ObjectId): InstructionAction = {
    PutCountersAction(counters, objectId)
  }
  def apply(number: Int, kind: Counter, objectPhrase: SingleIdentifyingNounPhrase[ObjectId]): IntransitiveInstructionVerb[PlayerId] = {
    Put(CountersLiteral(number, kind), objectPhrase)
  }
}
