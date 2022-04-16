package mtg.instructions.verbs

import mtg.actions.moveZone.MoveToHandAction
import mtg.core.zones.ZoneType
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.nounPhrases.{CardName, SingleIdentifyingNounPhrase}
import mtg.instructions.{InstructionResult, TransitiveInstructionVerb, Verb, VerbInflection}

object ReturnFromYourGraveyardToYourHand extends TransitiveInstructionVerb[PlayerId, ObjectId] {
  override def inflect(verbInflection: VerbInflection, cardName: String): String = Verb.Return.inflect(verbInflection, cardName)
  override def postObjectText: Option[String] = Some("from your graveyard to your hand")
  override def resolve(
    subject: PlayerId,
    obj: ObjectId,
    gameState: GameState,
    resolutionContext: StackObjectResolutionContext
  ): InstructionResult = {
    (MoveToHandAction(obj), resolutionContext)
  }

  override def getFunctionalZones(subjectPhrase: SingleIdentifyingNounPhrase[PlayerId], objectPhrase: SingleIdentifyingNounPhrase[ObjectId]): Option[Set[ZoneType]] = {
    if (objectPhrase == CardName) {
      Some(Set(ZoneType.Graveyard))
    } else {
      None
    }
  }
}
