package mtg.events

import mtg.game.PlayerIdentifier
import mtg.game.objects.ObjectId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class LoseLife(player: PlayerIdentifier, amount: Int) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.updateLifeTotal(player, _ - amount)
  }
}
