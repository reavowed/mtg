package mtg.game.start.mulligans

import mtg.events.{DrawCardsEvent, Event, EventResult}
import mtg.game.{GameData, PlayerIdentifier}
import mtg.game.objects.GameObjectState

case class DrawStartingHandsEvent(playersToDraw: Seq[PlayerIdentifier]) extends Event {
  override def execute(currentGameObjectState: GameObjectState, gameData: GameData): EventResult = {
    playersToDraw.map(DrawCardsEvent(_, 7))
  }
}
