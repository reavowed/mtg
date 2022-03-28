package mtg.instructions

import mtg.abilities.TriggerCondition
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.condition.Condition
import mtg.instructions.nouns.IndefiniteNounPhrase
import mtg.utils.CaseObjectWithName

trait TriggerWord extends CaseObjectWithName {
  def text: String = name.toLowerCase
  def apply(condition: Condition): TriggerCondition = {
    TriggerCondition(this, condition)
  }
  def apply(playerPhrase: IndefiniteNounPhrase[PlayerId], verb: IntransitiveEventMatchingVerb): TriggerCondition = {
    apply(IntransitiveEventMatchingVerb.WithSubject(playerPhrase, verb))
  }
  def apply(playerPhrase: IndefiniteNounPhrase[PlayerId], verb: TransitiveEventMatchingVerb, objectPhrase: IndefiniteNounPhrase[ObjectId]): TriggerCondition = {
    apply(playerPhrase, verb(objectPhrase))
  }
}
