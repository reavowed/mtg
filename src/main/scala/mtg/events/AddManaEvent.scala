package mtg.events

import mtg.game.PlayerId
import mtg.game.objects.ManaObject
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.parts.mana.ManaType

case class AddManaEvent(player: PlayerId, manaTypes: Seq[ManaType]) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateManaPool(player, _ ++ manaTypes.map(ManaObject))
  }
  override def canBeReverted: Boolean = true
}
