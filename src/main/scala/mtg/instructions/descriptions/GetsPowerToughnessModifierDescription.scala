package mtg.instructions.descriptions

import mtg.continuousEffects.{CharacteristicOrControlChangingContinuousEffect, ModifyPowerToughnessEffect}
import mtg.core.ObjectId
import mtg.effects.PowerToughnessModifier
import mtg.text.{Verb, VerbInflection}

case class GetsPowerToughnessModifierDescription(powerToughnessModifier: PowerToughnessModifier)
  extends CharacteristicOrControlChangingContinuousEffectDescription
{
  override def inflect(verbInflection: VerbInflection, cardName: String): String = Verb.Get.inflect(verbInflection, cardName) + " " + powerToughnessModifier.description
  override def getEffect(objectId: ObjectId): CharacteristicOrControlChangingContinuousEffect = ModifyPowerToughnessEffect(objectId, powerToughnessModifier)
}
