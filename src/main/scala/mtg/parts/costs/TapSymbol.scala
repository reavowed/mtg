package mtg.parts.costs

import mtg.events.TapObjectEvent
import mtg.game.state.{GameAction, ObjectWithState}

case object TapSymbol extends Symbol("T") with Cost {
  override def isUnpayable(abilitySource: ObjectWithState): Boolean = abilitySource.gameObject.permanentStatus.forall(_.isTapped)
  override def payForAbility(abilitySource: ObjectWithState): Seq[GameAction] = Seq(TapObjectEvent(abilitySource.gameObject))
}
