package mtg.actions.damage

import mtg.core.{ObjectId, ObjectOrPlayerId}
import mtg.game.state.{DelegatingGameObjectAction, GameActionResult, GameObjectAction, GameState}

case class DealCombatDamageAction(source: ObjectId, recipient: ObjectOrPlayerId, amount: Int) extends DelegatingGameObjectAction {
  override def delegate(implicit gameState: GameState): Seq[GameObjectAction] = {
    DealDamageAction(source, recipient, amount)
  }
}
