package mtg.events

import mtg.game.objects.CardObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}
import mtg.game.{PlayerIdentifier, Zone}

case class DrawCardEvent(playerIdentifier: PlayerIdentifier) extends GameObjectEvent {
  def execute(currentGameState: GameState): GameObjectEventResult = {
    val library = currentGameState.gameObjectState.libraries(playerIdentifier)
    library.dropWhile(o => !o.isInstanceOf[CardObject]).headOption match {
      case Some(topCard) =>
        Seq(MoveObjectEvent(topCard, Zone.Hand(playerIdentifier)))
      case None =>
        ()
    }
  }
}
