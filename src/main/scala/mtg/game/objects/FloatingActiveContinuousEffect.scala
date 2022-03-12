package mtg.game.objects

import mtg.continuousEffects.ContinuousEffect
import mtg.effects.condition.Condition

case class FloatingActiveContinuousEffect(effect: ContinuousEffect, endCondition: Condition)
