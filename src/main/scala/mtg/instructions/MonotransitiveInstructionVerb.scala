package mtg.instructions

import mtg.definitions.zones.ZoneType
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.SingleIdentifyingNounPhrase

trait MonotransitiveInstructionVerb[SubjectType, ObjectType] extends Verb {
  def postObjectText: Option[String] = None
  def apply(objectPhrase: SingleIdentifyingNounPhrase[ObjectType]): IntransitiveInstructionVerb[SubjectType] = {
    MonotransitiveInstructionVerbWithObject(this, objectPhrase)
  }
  def resolve(subject: SubjectType, obj: ObjectType): InstructionAction
  def getFunctionalZones(subjectPhrase: SingleIdentifyingNounPhrase[SubjectType], objectPhrase: SingleIdentifyingNounPhrase[ObjectType]): Option[Set[ZoneType]] = None
}

case class MonotransitiveInstructionVerbWithObject[SubjectType, ObjectType](
    verb: MonotransitiveInstructionVerb[SubjectType, ObjectType],
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
  override def resolve(subject: SubjectType): InstructionAction = {
    objectPhrase.identifySingle.flatMap(verb.resolve(subject, _))
  }

  override def getFunctionalZones(subjectPhrase: SingleIdentifyingNounPhrase[SubjectType]): Option[Set[ZoneType]] = {
    verb.getFunctionalZones(subjectPhrase, objectPhrase)
  }
}
