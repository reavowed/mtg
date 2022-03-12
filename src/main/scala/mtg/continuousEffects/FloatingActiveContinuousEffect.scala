package mtg.continuousEffects

import mtg.effects.condition.Condition

case class FloatingActiveContinuousEffect(effect: ContinuousEffect, endCondition: Condition)
