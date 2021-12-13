package mtg.effects.continuous

import mtg.effects.PowerToughnessModifier
import mtg.game.ObjectId

case class ModifyPowerToughnessEffect(affectedObject: ObjectId, powerToughnessModifier: PowerToughnessModifier)
  extends CharacteristicOrControlChangingContinuousEffect
