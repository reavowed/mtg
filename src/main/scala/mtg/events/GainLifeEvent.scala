package mtg.events

import mtg.game.PlayerIdentifier
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class GainLifeEvent(player: PlayerIdentifier, amount: Int) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.updateLifeTotal(player, _ + amount)
  }
}
