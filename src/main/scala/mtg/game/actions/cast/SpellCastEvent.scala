package mtg.game.actions.cast

import mtg.game.ObjectId
import mtg.game.state.{GameState, InternalGameAction, GameActionResult}

case class SpellCastEvent(spellId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    // No actual effect, just marking the spell as cast
    ()
  }
  override def canBeReverted: Boolean = true
}
