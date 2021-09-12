package mtg.game.actions.cast

import mtg.game.ObjectId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState, InternalGameAction, InternalGameActionResult}

case class SpellCastEvent(spellId: ObjectId) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    // No actual effect, just marking the spell as cast
    ()
  }
  override def canBeReverted: Boolean = true
}
