package mtg.actions

import mtg.core.PlayerId
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class DrawCardsAction(playerIdentifier: PlayerId, numberOfCards: Int) extends InternalGameAction {
  def execute(gameState: GameState): GameActionResult = {
    Seq.fill(numberOfCards)(DrawCardAction(playerIdentifier))
  }
  override def canBeReverted: Boolean = false
}
