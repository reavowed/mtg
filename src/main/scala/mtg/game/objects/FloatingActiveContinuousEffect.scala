package mtg.game.objects

import mtg.effects.ContinuousEffect
import mtg.effects.condition.Condition

case class FloatingActiveContinuousEffect(effect: ContinuousEffect, endCondition: Condition)
