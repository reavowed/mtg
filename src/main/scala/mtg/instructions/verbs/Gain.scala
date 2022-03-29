package mtg.instructions.verbs

import mtg.abilities.AbilityDefinition
import mtg.continuousEffects.{AddAbilityEffect, CharacteristicOrControlChangingContinuousEffect}
import mtg.core.ObjectId
import mtg.instructions.CharacteristicChangingVerb
import mtg.text.{Verb, VerbInflection}

case class Gain(abilityDefinition: AbilityDefinition) extends CharacteristicChangingVerb {
  override def inflect(verbInflection: VerbInflection, cardName: String): String = {
    Verb.Gain.inflect(verbInflection, cardName) + " " + abilityDefinition.getQuotedDescription(cardName)
  }
  override def getEffects(objectId: ObjectId): Seq[CharacteristicOrControlChangingContinuousEffect] = {
    Seq(AddAbilityEffect(objectId, abilityDefinition))
  }
}
