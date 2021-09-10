package mtg.events

import mtg.game.PlayerId
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}

case class DrawCardsAction(playerIdentifier: PlayerId, numberOfCards: Int) extends GameObjectAction {
  def execute(currentGameState: GameState): GameObjectActionResult = {
    Seq.fill(numberOfCards)(DrawCardAction(playerIdentifier))
  }
}
