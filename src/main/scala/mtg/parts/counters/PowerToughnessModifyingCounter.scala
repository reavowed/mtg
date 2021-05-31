package mtg.parts.counters

class PowerToughnessModifyingCounter(val powerModifier: Int, val toughnessModifier: Int) extends CounterType {
  override def description: String = {
    def getDescription(modifier: Int) = if (modifier >= 0) "+" + modifier else modifier.toString
    getDescription(powerModifier) + "/" + getDescription(toughnessModifier)
  }
}
