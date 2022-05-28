package mtg.parts

import mtg.effects.PowerToughnessModifier

sealed trait Counter {
  def description: String
}

object Counter {
  abstract class PowerToughnessModifying(val modifier: PowerToughnessModifier) extends Counter {
    override def description: String = modifier.description
  }

  case object PlusOnePlusOne extends PowerToughnessModifying((1, 1))
}
