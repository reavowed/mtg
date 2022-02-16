package mtg.events

import mtg.core.PlayerId
import mtg.events.moveZone.MoveToHandEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class DrawCardEvent(player: PlayerId) extends InternalGameAction {
  def execute(gameState: GameState): GameActionResult = {
    val library = gameState.gameObjectState.libraries(player)
    library.dropWhile(!_.isCard).headOption match {
      case Some(topCard) =>
        MoveToHandEvent(topCard.objectId)
      case None =>
        ()
    }
  }
  override def canBeReverted: Boolean = false
}
