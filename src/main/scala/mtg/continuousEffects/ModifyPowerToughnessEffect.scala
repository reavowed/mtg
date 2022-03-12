package mtg.continuousEffects

import mtg.core.ObjectId
import mtg.effects.PowerToughnessModifier

case class ModifyPowerToughnessEffect(affectedObject: ObjectId, powerToughnessModifier: PowerToughnessModifier)
  extends CharacteristicOrControlChangingContinuousEffect
