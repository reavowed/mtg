package mtg.events

import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}
import mtg.game.{PlayerId, Zone}

case class DrawCardAction(player: PlayerId) extends GameObjectAction {
  def execute(currentGameState: GameState): GameObjectActionResult = {
    val library = currentGameState.gameObjectState.libraries(player)
    library.dropWhile(!_.isCard).headOption match {
      case Some(topCard) =>
        MoveObjectAction(player, topCard, Zone.Hand(player))
      case None =>
        ()
    }
  }
}
