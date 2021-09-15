package mtg.events

import mtg.game.PlayerId
import mtg.game.state.{InternalGameAction, GameActionResult, GameState}

case class GainLifeEvent(player: PlayerId, amount: Int) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateLifeTotal(player, _ + amount)
  }
  override def canBeReverted: Boolean = true
}
