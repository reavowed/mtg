package mtg.instructions.suffixDescriptors

import mtg.actions.moveZone.MoveToBattlefieldAction
import mtg.instructions.EntersTheBattlefieldModifier
import mtg.parts.counters.{CounterSpecification, CounterType}

case class WithCountersOnIt(counterSpecification: CounterSpecification) extends EntersTheBattlefieldModifier {
  override def getText(cardName: String): String = s"with ${counterSpecification.description} on it"
  override def modifyAction(action: MoveToBattlefieldAction): MoveToBattlefieldAction = {
    action.copy(counters = counterSpecification.addToMap(action.counters))
  }
}

object WithCountersOnIt {
  def apply(number: Int, kind: CounterType): WithCountersOnIt = {
    WithCountersOnIt(CounterSpecification(number, kind))
  }
}
