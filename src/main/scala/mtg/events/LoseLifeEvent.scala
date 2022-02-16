package mtg.events

import mtg.core.PlayerId
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class LoseLifeEvent(player: PlayerId, amount: Int) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateLifeTotal(player, _ - amount)
  }
  override def canBeReverted: Boolean = true
}
