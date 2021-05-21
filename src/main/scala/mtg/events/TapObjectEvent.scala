package mtg.events

import mtg.game.objects.GameObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class TapObjectEvent(gameObject: GameObject) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.updateGameObject(
      gameObject,
      gameObject.updatePermanentStatus(_.tap()))
  }
}
