package mtg.events

import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}
import mtg.game.{PlayerId, Zone}

case class DrawCardEvent(player: PlayerId) extends GameObjectEvent {
  def execute(currentGameState: GameState): GameObjectEventResult = {
    val library = currentGameState.gameObjectState.libraries(player)
    library.dropWhile(!_.isCard).headOption match {
      case Some(topCard) =>
        MoveObjectEvent(player, topCard, Zone.Hand(player))
      case None =>
        ()
    }
  }
  override def canBeReverted: Boolean = false
}
