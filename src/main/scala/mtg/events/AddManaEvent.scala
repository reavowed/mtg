package mtg.events

import mtg.game.PlayerId
import mtg.game.objects.ManaObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}
import mtg.parts.mana.ManaType

case class AddManaEvent(player: PlayerId, manaTypes: Seq[ManaType]) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.updateManaPool(player, _ ++ manaTypes.map(ManaObject))
  }
}
