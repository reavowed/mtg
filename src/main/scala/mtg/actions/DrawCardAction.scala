package mtg.actions

import mtg.core.PlayerId
import mtg.actions.moveZone.MoveToHandAction
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class DrawCardAction(player: PlayerId) extends InternalGameAction {
  def execute(gameState: GameState): GameActionResult = {
    val library = gameState.gameObjectState.libraries(player)
    library.dropWhile(!_.isCard).headOption match {
      case Some(topCard) =>
        MoveToHandAction(topCard.objectId)
      case None =>
        ()
    }
  }
  override def canBeReverted: Boolean = false
}