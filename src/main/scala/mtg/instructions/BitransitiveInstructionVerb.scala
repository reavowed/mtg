package mtg.instructions

import mtg.definitions.zones.ZoneType
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.SingleIdentifyingNounPhrase

trait BitransitiveInstructionVerb[SubjectType, DirectObjectType, IndirectObjectType] extends Verb {
  def objectJoiningText: String
  def apply(
    directObjectPhrase: SingleIdentifyingNounPhrase[DirectObjectType],
    indirectObjectPhrase: SingleIdentifyingNounPhrase[IndirectObjectType]
  ): IntransitiveInstructionVerb[SubjectType] = {
    BitransitiveInstructionVerbWithObjects(this, directObjectPhrase, indirectObjectPhrase)
  }
  def resolve(subject: SubjectType, directObject: DirectObjectType, indirectObject: IndirectObjectType): InstructionAction
  def getFunctionalZones(
    subjectPhrase: SingleIdentifyingNounPhrase[SubjectType],
    directObjectPhrase: SingleIdentifyingNounPhrase[DirectObjectType],
    indirectObjectPhrase: SingleIdentifyingNounPhrase[IndirectObjectType]
  ): Option[Set[ZoneType]] = None
}

case class BitransitiveInstructionVerbWithObjects[SubjectType, DirectObjectType, IndirectObjectType](
    verb: BitransitiveInstructionVerb[SubjectType, DirectObjectType, IndirectObjectType],
    directObjectPhrase: SingleIdentifyingNounPhrase[DirectObjectType],
    indirectObjectPhrase: SingleIdentifyingNounPhrase[IndirectObjectType])
  extends IntransitiveInstructionVerb[SubjectType]
{
  override def inflect(verbInflection: VerbInflection, cardName: String): String = {
    Seq(
      verb.inflect(verbInflection, cardName),
      directObjectPhrase.getText(cardName),
      verb.objectJoiningText,
      indirectObjectPhrase.getText(cardName)
    ).mkString(" ")
  }
  override def resolve(subject: SubjectType): InstructionAction = {
    directObjectPhrase.identifySingle.flatMap { directObject =>
      indirectObjectPhrase.identifySingle.flatMap { indirectObject =>
        verb.resolve(subject, directObject, indirectObject)
      }
    }
  }

  override def getFunctionalZones(subjectPhrase: SingleIdentifyingNounPhrase[SubjectType]): Option[Set[ZoneType]] = {
    verb.getFunctionalZones(subjectPhrase, directObjectPhrase, indirectObjectPhrase)
  }
}
