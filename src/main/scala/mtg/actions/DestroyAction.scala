package mtg.actions

import mtg.actions.moveZone.MoveToGraveyardAction
import mtg.definitions.ObjectId
import mtg.game.state.{DelegatingGameObjectAction, GameObjectAction, GameState}

case class DestroyAction(objectId: ObjectId) extends DelegatingGameObjectAction {
  override def delegate(implicit gameState: GameState): Seq[GameObjectAction[_]] = {
    MoveToGraveyardAction(objectId)
  }
}
