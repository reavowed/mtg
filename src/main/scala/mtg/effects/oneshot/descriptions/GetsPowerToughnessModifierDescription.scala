package mtg.effects.oneshot.descriptions

import mtg.effects.continuous.ModifyPowerToughnessEffect
import mtg.effects.{ContinuousObjectEffect, EffectContext, PowerToughnessModifier}
import mtg.game.ObjectId

case class GetsPowerToughnessModifierDescription(powerToughnessModifier: PowerToughnessModifier) extends ContinuousEffectDescription {
  override def getText(cardName: String): String = "gets " + powerToughnessModifier.description
  override def getEffect(objectId: ObjectId): ContinuousObjectEffect = ModifyPowerToughnessEffect(objectId, powerToughnessModifier)
}
