package mtg.effects.oneshot.descriptions

import mtg.effects.continuous.ModifyPowerToughnessEffect
import mtg.effects.{ContinuousEffect, PowerToughnessModifier}
import mtg.game.ObjectId
import mtg.text.{VerbPhraseTemplate, Verbs}

case class GetsPowerToughnessModifierDescription(powerToughnessModifier: PowerToughnessModifier) extends ContinuousEffectDescription {
  override def getVerbPhraseTemplate(cardName: String): VerbPhraseTemplate = Verbs.Get.withSuffix(powerToughnessModifier.description)
  override def getEffect(objectId: ObjectId): ContinuousEffect = ModifyPowerToughnessEffect(objectId, powerToughnessModifier)
}
