package mtg.parts.costs

import mtg.actions.TapObjectAction
import mtg.core.symbols.Symbol
import mtg.game.objects.PermanentObject
import mtg.game.state.{GameAction, ObjectWithState, WrappedOldUpdates}

case object TapSymbol extends Symbol("T") with Cost {
  override def isUnpayable(objectWithAbility: ObjectWithState): Boolean = objectWithAbility.gameObject.asOptionalInstanceOf[PermanentObject].exists(_.status.isTapped)
  override def payForAbility(objectWithAbility: ObjectWithState): GameAction[Any] = WrappedOldUpdates(TapObjectAction(objectWithAbility.gameObject.objectId))
}
