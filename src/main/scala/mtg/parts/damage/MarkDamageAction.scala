package mtg.parts.damage

import mtg.game.ObjectId
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}

case class MarkDamageAction(sourceId: ObjectId, creatureId: ObjectId, amount: Int) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.updatePermanentObject(creatureId, _.updateMarkedDamage(_ + amount))
  }
}
