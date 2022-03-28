package mtg.instructions.descriptions

import mtg.abilities.AbilityDefinition
import mtg.continuousEffects.{AddAbilityEffect, CharacteristicOrControlChangingContinuousEffect}
import mtg.core.ObjectId
import mtg.text.{Verb, VerbInflection}

case class GainsAbilityDescription(ability: AbilityDefinition)
  extends CharacteristicOrControlChangingContinuousEffectDescription
{
  override def inflect(verbInflection: VerbInflection, cardName: String): String = Verb.Gain.inflect(verbInflection, cardName) + " " + ability.getQuotedDescription(cardName)
  override def getEffect(objectId: ObjectId): CharacteristicOrControlChangingContinuousEffect = AddAbilityEffect(objectId, ability)
}
