package mtg.game.start.mulligans

import mtg.events.DrawCardsEvent
import mtg.game.PlayerId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class DrawStartingHandsEvent(playersToDraw: Seq[PlayerId]) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    playersToDraw.map(DrawCardsEvent(_, currentGameState.gameData.startingHandSize))
  }
  override def canBeReverted: Boolean = false
}
