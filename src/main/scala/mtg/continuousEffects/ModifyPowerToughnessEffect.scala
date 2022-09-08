package mtg.continuousEffects

import mtg.definitions.ObjectId
import mtg.effects.PowerToughnessModifier

case class ModifyPowerToughnessEffect(affectedObject: ObjectId, powerToughnessModifier: PowerToughnessModifier)
  extends CharacteristicOrControlChangingContinuousEffect
