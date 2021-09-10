package mtg.game.start.mulligans

import mtg.events.DrawCardsAction
import mtg.game.PlayerId
import mtg.game.state.history.GameEvent
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}

case class DrawStartingHandsAction(playersToDraw: Seq[PlayerId]) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    (playersToDraw.map(DrawCardsAction(_, currentGameState.gameData.startingHandSize)), DrawStartingHandsEvent)
  }
}

object DrawStartingHandsEvent extends GameEvent
