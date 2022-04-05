package mtg.actions

import mtg.core.PlayerId
import mtg.game.state.{GameActionResult, GameState, GameObjectAction}

case class DrawCardsAction(playerIdentifier: PlayerId, numberOfCards: Int) extends GameObjectAction {
  def execute(gameState: GameState): GameActionResult = {
    Seq.fill(numberOfCards)(DrawCardAction(playerIdentifier))
  }
  override def canBeReverted: Boolean = false
}
