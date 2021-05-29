package mtg.game.turns.turnBasedActions

import mtg.events.UntapObjectEvent
import mtg.game.Zone
import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}

case object UntapForTurn extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val tappedPermanents = currentGameState.gameObjectState.derivedState.permanentStates.values.view
      .filter(o => o.gameObject.zone == Zone.Battlefield && o.controller == currentGameState.activePlayer)
      .map(_.gameObject.objectId)
      .toSeq
    tappedPermanents.map(UntapObjectEvent)
  }
}
