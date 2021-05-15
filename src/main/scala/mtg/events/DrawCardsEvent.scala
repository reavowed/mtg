package mtg.events

import mtg.game.PlayerIdentifier
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class DrawCardsEvent(playerIdentifier: PlayerIdentifier, numberOfCards: Int) extends GameObjectEvent {
  def execute(currentGameState: GameState): GameObjectEventResult = {
    Seq.fill(numberOfCards)(DrawCardEvent(playerIdentifier))
  }
}
