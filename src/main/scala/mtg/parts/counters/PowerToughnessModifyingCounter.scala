package mtg.parts.counters

import mtg.effects.PowerToughnessModifier

abstract class PowerToughnessModifyingCounter(val modifier: PowerToughnessModifier) extends CounterType {
  override def description: String = modifier.description
}
