package mtg.events

import mtg.game.objects.{CardObject, GameObjectState}
import mtg.game.{GameData, PlayerIdentifier, Zone}

case class DrawCardEvent(playerIdentifier: PlayerIdentifier) extends Event {
  def execute(currentGameObjectState: GameObjectState, gameData: GameData): EventResult = {
    val library = currentGameObjectState.libraries(playerIdentifier)
    library.dropWhile(o => !o.isInstanceOf[CardObject]).headOption match {
      case Some(topCard) =>
        Seq(MoveObjectEvent(topCard, Zone.Hand(playerIdentifier)))
      case None =>
        ()
    }
  }
}
