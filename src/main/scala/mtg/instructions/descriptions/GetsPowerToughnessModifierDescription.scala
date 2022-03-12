package mtg.instructions.descriptions

import mtg.continuousEffects.{CharacteristicOrControlChangingContinuousEffect, ModifyPowerToughnessEffect}
import mtg.core.ObjectId
import mtg.effects.PowerToughnessModifier
import mtg.text.{VerbPhraseTemplate, Verbs}

case class GetsPowerToughnessModifierDescription(powerToughnessModifier: PowerToughnessModifier)
  extends CharacteristicOrControlChangingContinuousEffectDescription
{
  override def getVerbPhraseTemplate(cardName: String): VerbPhraseTemplate = Verbs.Get.withSuffix(powerToughnessModifier.description)
  override def getEffect(objectId: ObjectId): CharacteristicOrControlChangingContinuousEffect = ModifyPowerToughnessEffect(objectId, powerToughnessModifier)
}
