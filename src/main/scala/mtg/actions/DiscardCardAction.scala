package mtg.actions

import mtg.actions.moveZone.MoveToGraveyardAction
import mtg.core.{ObjectId, PlayerId}
import mtg.game.state.{DelegatingGameObjectAction, GameObjectAction, GameState}

case class DiscardCardAction(player: PlayerId, objectId: ObjectId) extends DelegatingGameObjectAction {
  override def delegate(implicit gameState: GameState): Seq[GameObjectAction[_]] = {
    MoveToGraveyardAction(objectId)
  }
}
