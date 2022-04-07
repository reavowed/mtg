package mtg.parts.costs

import mtg.actions.TapObjectAction
import mtg.core.symbols.Symbol
import mtg.game.objects.PermanentObject
import mtg.game.state.{GameAction, ObjectWithState}

case object TapSymbol extends Symbol("T") with Cost {
  override def isUnpayable(objectWithAbility: ObjectWithState): Boolean = objectWithAbility.gameObject.asOptionalInstanceOf[PermanentObject].exists(_.status.isTapped)
  override def payForAbility(objectWithAbility: ObjectWithState): GameAction[Any] = TapObjectAction(objectWithAbility.gameObject.objectId)
}
