package mtg.actions

import mtg.core.PlayerId
import mtg.game.objects.GameObjectState
import mtg.game.state.{DirectGameObjectAction, GameState}

case class GainLifeAction(player: PlayerId, amount: Int) extends DirectGameObjectAction {
  override def execute(implicit gameState: GameState): GameObjectState = {
    gameState.gameObjectState.updateLifeTotal(player, _ + amount)
  }
  override def canBeReverted: Boolean = true
}
