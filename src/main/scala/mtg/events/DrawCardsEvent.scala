package mtg.events

import mtg.game.PlayerId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class DrawCardsEvent(playerIdentifier: PlayerId, numberOfCards: Int) extends GameObjectEvent {
  def execute(currentGameState: GameState): GameObjectEventResult = {
    Seq.fill(numberOfCards)(DrawCardEvent(playerIdentifier))
  }
  override def canBeReverted: Boolean = false
}
