package mtg.effects.continuous

import mtg.effects.{ContinuousObjectEffect, PowerToughnessModifier}
import mtg.game.ObjectId

case class ModifyPowerToughnessEffect(affectedObject: ObjectId, powerToughnessModifier: PowerToughnessModifier) extends ContinuousObjectEffect
