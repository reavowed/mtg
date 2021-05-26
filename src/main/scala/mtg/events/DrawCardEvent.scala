package mtg.events

import mtg.game.objects.CardObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}
import mtg.game.{PlayerIdentifier, Zone}

case class DrawCardEvent(player: PlayerIdentifier) extends GameObjectEvent {
  def execute(currentGameState: GameState): GameObjectEventResult = {
    val library = currentGameState.gameObjectState.libraries(player)
    library.dropWhile(o => !o.isInstanceOf[CardObject]).headOption match {
      case Some(topCard) =>
        MoveObjectEvent(player, topCard, Zone.Hand(player))
      case None =>
        ()
    }
  }
}
