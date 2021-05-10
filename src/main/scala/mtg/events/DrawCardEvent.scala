package mtg.events

import mtg.game.`object`.CardObject
import mtg.game.zone.Hand
import mtg.game.{GameState, PlayerIdentifier}

case class DrawCardEvent(playerIdentifier: PlayerIdentifier) extends Event {
  override def execute(currentGameState: GameState): Either[GameState, Seq[Event]] = {
    val library = currentGameState.zoneStates.libraries(playerIdentifier)
    library.objects.dropWhile(o => !o.isInstanceOf[CardObject]).headOption match {
      case Some(topCard) =>
        Right(Seq(MoveObjectEvent(topCard, Hand(playerIdentifier))))
      case None =>
        Left(currentGameState)
    }
  }
}
