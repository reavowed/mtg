package mtg.instructions

import mtg.abilities.TriggerCondition
import mtg.definitions.ObjectOrPlayerId
import mtg.effects.condition.Condition
import mtg.instructions.nounPhrases.IndefiniteNounPhrase
import mtg.utils.CaseObjectWithName

trait TriggerWord extends CaseObjectWithName {
  def text: String = name.toLowerCase
  def apply(condition: Condition): TriggerCondition = {
    TriggerCondition(this, condition)
  }
  def apply[SubjectType <: ObjectOrPlayerId](subjectPhrase: IndefiniteNounPhrase[SubjectType], verb: IntransitiveEventMatchingVerb[SubjectType]): TriggerCondition = {
    apply(IntransitiveEventMatchingVerb.WithSubject(subjectPhrase, verb))
  }
  def apply[SubjectType <: ObjectOrPlayerId, ObjectType <: ObjectOrPlayerId](
    subjectPhrase: IndefiniteNounPhrase[SubjectType],
    verb: TransitiveEventMatchingVerb[SubjectType, ObjectType],
    objectPhrase: IndefiniteNounPhrase[ObjectType]
  ): TriggerCondition = {
    apply(subjectPhrase, verb(objectPhrase))
  }
}
