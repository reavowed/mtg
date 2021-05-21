package mtg.parts.costs

import mtg.events.TapObjectEvent
import mtg.game.state.{GameAction, ObjectWithState}

case object TapSymbol extends Symbol("T") with Cost {
  override def payForAbility(abilitySource: ObjectWithState): Seq[GameAction] = Seq(TapObjectEvent(abilitySource.gameObject))
}
