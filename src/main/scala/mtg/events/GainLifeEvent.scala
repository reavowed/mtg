package mtg.events

import mtg.game.PlayerId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class GainLifeEvent(player: PlayerId, amount: Int) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.updateLifeTotal(player, _ + amount)
  }
  override def canBeReverted: Boolean = true
}
