package mtg.effects.oneshot.descriptions

import mtg.abilities.AbilityDefinition
import mtg.continuousEffects.{AddAbilityEffect, CharacteristicOrControlChangingContinuousEffect}
import mtg.core.ObjectId
import mtg.text.{VerbPhraseTemplate, Verbs}

case class GainsAbilityDescription(ability: AbilityDefinition)
  extends CharacteristicOrControlChangingContinuousEffectDescription
{
  override def getVerbPhraseTemplate(cardName: String): VerbPhraseTemplate = Verbs.Gain.withSuffix(ability.getQuotedDescription(cardName))
  override def getEffect(objectId: ObjectId): CharacteristicOrControlChangingContinuousEffect = AddAbilityEffect(objectId, ability)
}
