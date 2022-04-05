package mtg.actions

import mtg.core.PlayerId
import mtg.game.state.{GameActionResult, GameState, GameObjectAction}

case class LoseLifeAction(player: PlayerId, amount: Int) extends GameObjectAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateLifeTotal(player, _ - amount)
  }
  override def canBeReverted: Boolean = true
}
