package mtg.game.turns.turnBasedActions

import mtg.events.UntapObjectEvent
import mtg.game.Zone
import mtg.game.state.{GameState, InternalGameAction, GameActionResult}

case object UntapForTurn extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    val tappedPermanents = gameState.gameObjectState.derivedState.permanentStates.values.view
      .filter(o => o.gameObject.zone == Zone.Battlefield && o.controller == gameState.activePlayer)
      .map(_.gameObject.objectId)
      .toSeq
    tappedPermanents.map(UntapObjectEvent)
  }
  override def canBeReverted: Boolean = true
}
