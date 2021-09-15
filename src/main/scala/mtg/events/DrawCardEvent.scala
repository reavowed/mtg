package mtg.events

import mtg.game.state.{InternalGameAction, GameActionResult, GameState}
import mtg.game.{PlayerId, Zone}

case class DrawCardEvent(player: PlayerId) extends InternalGameAction {
  def execute(gameState: GameState): GameActionResult = {
    val library = gameState.gameObjectState.libraries(player)
    library.dropWhile(!_.isCard).headOption match {
      case Some(topCard) =>
        MoveObjectEvent(player, topCard, Zone.Hand(player))
      case None =>
        ()
    }
  }
  override def canBeReverted: Boolean = false
}
