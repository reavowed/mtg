package mtg.events

import mtg.game.PlayerId
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}

case class LoseLifeAction(player: PlayerId, amount: Int) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.updateLifeTotal(player, _ - amount)
  }
}
