package mtg.game.start.mulligans

import mtg.events.DrawCardsEvent
import mtg.game.PlayerId
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class DrawStartingHandsEvent(playersToDraw: Seq[PlayerId], mulligansAlready: Int) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    playersToDraw.map(DrawCardsEvent(_, gameState.gameData.startingHandSize))
  }
  override def canBeReverted: Boolean = false
}
