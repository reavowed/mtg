package mtg.events

import mtg.game.objects.GameObjectState
import mtg.game.{GameData, PlayerIdentifier}

case class DrawCardsEvent(playerIdentifier: PlayerIdentifier, numberOfCards: Int) extends Event {
  def execute(currentGameObjectState: GameObjectState, gameData: GameData): EventResult = {
    Seq.fill(numberOfCards)(DrawCardEvent(playerIdentifier))
  }
}
