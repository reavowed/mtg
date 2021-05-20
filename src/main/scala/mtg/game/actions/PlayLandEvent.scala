package mtg.game.actions

import mtg.events.MoveObjectEvent
import mtg.game.Zone
import mtg.game.objects.{CardObject, GameObject}
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class PlayLandEvent(landCard: GameObject) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    Seq(MoveObjectEvent(landCard, Zone.Battlefield))
  }
}
