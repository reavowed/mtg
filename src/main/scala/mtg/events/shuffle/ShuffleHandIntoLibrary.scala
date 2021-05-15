package mtg.events.shuffle

import mtg.events.{Event, EventResult, MoveObjectEvent}
import mtg.game.{GameData, PlayerIdentifier, Zone}
import mtg.game.objects.GameObjectState

case class ShuffleHandIntoLibrary(playerIdentifier: PlayerIdentifier) extends Event {
  override def execute(currentGameObjectState: GameObjectState, gameData: GameData): EventResult = {
    currentGameObjectState.hands(playerIdentifier).map(MoveObjectEvent(_, Zone.Library(playerIdentifier))) :+ ShuffleLibrary(playerIdentifier)
  }
}
