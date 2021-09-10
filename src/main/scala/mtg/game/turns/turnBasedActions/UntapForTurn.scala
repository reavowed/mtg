package mtg.game.turns.turnBasedActions

import mtg.events.UntapObjectAction
import mtg.game.Zone
import mtg.game.state.{GameState, InternalGameAction, GameActionResult}

case object UntapForTurn extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    val tappedPermanents = currentGameState.gameObjectState.derivedState.permanentStates.values.view
      .filter(o => o.gameObject.zone == Zone.Battlefield && o.controller == currentGameState.activePlayer)
      .map(_.gameObject.objectId)
      .toSeq
    tappedPermanents.map(UntapObjectAction)
  }
}
