package mtg.events

import mtg.game.PlayerId
import mtg.game.objects.ManaObject
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}
import mtg.parts.mana.ManaType

case class AddManaAction(player: PlayerId, manaTypes: Seq[ManaType]) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.updateManaPool(player, _ ++ manaTypes.map(ManaObject))
  }
}
