package mtg.instructions

import mtg.core.zones.ZoneType
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.SingleIdentifyingNounPhrase

trait TransitiveInstructionVerb[SubjectType, ObjectType] extends Verb {
  def postObjectText: Option[String] = None
  def apply(objectPhrase: SingleIdentifyingNounPhrase[ObjectType]): IntransitiveInstructionVerb[SubjectType] = {
    TransitiveInstructionVerbWithObject(this, objectPhrase)
  }
  def resolve(subject: SubjectType, obj: ObjectType, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult
  def getFunctionalZones(subjectPhrase: SingleIdentifyingNounPhrase[SubjectType], objectPhrase: SingleIdentifyingNounPhrase[ObjectType]): Option[Set[ZoneType]] = None
}

case class TransitiveInstructionVerbWithObject[SubjectType, ObjectType](
    verb: TransitiveInstructionVerb[SubjectType, ObjectType],
    objectPhrase: SingleIdentifyingNounPhrase[ObjectType])
  extends IntransitiveInstructionVerb[SubjectType]
{
  override def inflect(verbInflection: VerbInflection, cardName: String): String = {
    val mainText = verb.inflect(verbInflection, cardName) + " " + objectPhrase.getText(cardName)
    verb.postObjectText match {
      case Some(postObjectText) =>
        mainText + " " + postObjectText
      case None =>
        mainText
    }
  }
  override def resolve(subject: SubjectType, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (objectId, contextAfterObjects) = objectPhrase.identifySingle(gameState, resolutionContext)
    verb.resolve(subject, objectId, gameState, contextAfterObjects)
  }

  override def getFunctionalZones(subjectPhrase: SingleIdentifyingNounPhrase[SubjectType]): Option[Set[ZoneType]] = {
    verb.getFunctionalZones(subjectPhrase, objectPhrase)
  }
}
