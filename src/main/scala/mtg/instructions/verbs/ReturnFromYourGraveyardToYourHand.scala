package mtg.instructions.verbs

import mtg.actions.moveZone.MoveToHandAction
import mtg.definitions.zones.ZoneType
import mtg.definitions.{ObjectId, PlayerId}
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.{CardName, SingleIdentifyingNounPhrase}
import mtg.instructions.{InstructionAction, MonotransitiveInstructionVerb, Verb}

object ReturnFromYourGraveyardToYourHand extends MonotransitiveInstructionVerb[PlayerId, ObjectId] {
  override def inflect(verbInflection: VerbInflection, cardName: String): String = Verb.Return.inflect(verbInflection, cardName)
  override def postObjectText: Option[String] = Some("from your graveyard to your hand")
  override def resolve(subject: PlayerId, obj: ObjectId): InstructionAction = {
    MoveToHandAction(obj)
  }

  override def getFunctionalZones(subjectPhrase: SingleIdentifyingNounPhrase[PlayerId], objectPhrase: SingleIdentifyingNounPhrase[ObjectId]): Option[Set[ZoneType]] = {
    if (objectPhrase == CardName) {
      Some(Set(ZoneType.Graveyard))
    } else {
      None
    }
  }
}
