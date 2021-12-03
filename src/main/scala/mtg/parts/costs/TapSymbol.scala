package mtg.parts.costs

import mtg.events.TapObjectEvent
import mtg.game.objects.PermanentObject
import mtg.game.state.{GameUpdate, ObjectWithState, OldGameUpdate}

case object TapSymbol extends Symbol("T") with Cost {
  override def isUnpayable(objectWithAbility: ObjectWithState): Boolean = objectWithAbility.gameObject.asOptionalInstanceOf[PermanentObject].exists(_.status.isTapped)
  override def payForAbility(objectWithAbility: ObjectWithState): Seq[OldGameUpdate] = Seq(TapObjectEvent(objectWithAbility.gameObject.objectId))
}
