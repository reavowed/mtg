package mtg.game.objects

import mtg.effects.ContinuousObjectEffect
import mtg.effects.condition.Condition

case class FloatingActiveContinuousEffect(effect: ContinuousObjectEffect, endCondition: Condition)
