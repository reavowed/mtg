package mtg.actions

import mtg.definitions.PlayerId
import mtg.game.state.{DirectGameObjectAction, GameState}

case class LoseLifeAction(player: PlayerId, amount: Int) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    gameState.gameObjectState.updateLifeTotal(player, _ - amount)
  }
  override def canBeReverted: Boolean = true
}
