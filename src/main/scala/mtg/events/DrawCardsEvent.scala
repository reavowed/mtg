package mtg.events

import mtg.game.PlayerId
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class DrawCardsEvent(playerIdentifier: PlayerId, numberOfCards: Int) extends InternalGameAction {
  def execute(gameState: GameState): GameActionResult = {
    Seq.fill(numberOfCards)(DrawCardEvent(playerIdentifier))
  }
  override def canBeReverted: Boolean = false
}
