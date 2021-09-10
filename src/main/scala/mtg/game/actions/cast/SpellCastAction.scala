package mtg.game.actions.cast

import mtg.game.ObjectId
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}

case class SpellCastAction(spellId: ObjectId) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    // No actual effect, just marking the spell as cast
    ()
  }
}
