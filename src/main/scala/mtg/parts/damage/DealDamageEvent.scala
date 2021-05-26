package mtg.parts.damage

import mtg.game.objects.ObjectId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class DealDamageEvent(source: ObjectId, recipient: DamageRecipient, amount: Int) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = recipient.getDamageResult(this)
}
