package mtg.actions

import mtg.actions.moveZone.MoveToHandAction
import mtg.definitions.PlayerId
import mtg.game.state.{DelegatingGameObjectAction, GameObjectAction, GameState}

case class DrawCardAction(player: PlayerId) extends DelegatingGameObjectAction {
  override def delegate(implicit gameState: GameState): Seq[GameObjectAction[_]] = {
    val library = gameState.gameObjectState.libraries(player)
    library.dropWhile(!_.isCard).headOption.toSeq.map(c => MoveToHandAction(c.objectId))
  }
}
