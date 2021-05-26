package mtg.events

import mtg.game.PlayerIdentifier
import mtg.game.objects.ObjectId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class DealDamage(source: ObjectId, player: PlayerIdentifier, amount: Int) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = LoseLife(player, amount)
}
