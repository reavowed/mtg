package mtg.effects.continuous

import mtg.abilities.AbilityDefinition
import mtg.effects.{ContinuousEffect, PowerToughnessModifier}
import mtg.game.ObjectId

case class ModifyPowerToughnessEffect(affectedObject: ObjectId, powerToughnessModifier: PowerToughnessModifier) extends ContinuousEffect
