package mtg.parts.costs

import mtg.events.TapObjectAction
import mtg.game.objects.PermanentObject
import mtg.game.state.{GameAction, ObjectWithState, PermanentObjectWithState}

case object TapSymbol extends Symbol("T") with Cost {
  override def isUnpayable(objectWithAbility: ObjectWithState): Boolean = objectWithAbility.gameObject.asOptionalInstanceOf[PermanentObject].exists(_.status.isTapped)
  override def payForAbility(objectWithAbility: ObjectWithState): Seq[GameAction] = Seq(TapObjectAction(objectWithAbility.gameObject.objectId))
}
