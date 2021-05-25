package mtg.game.turns.turnBasedActions

import mtg.events.UntapObjectEvent
import mtg.game.Zone
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

case object UntapForTurn extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val tappedPermanents = currentGameState.derivedState.allObjectStates.view
      .filter(o => o.gameObject.zone == Zone.Battlefield && o.controller.contains(currentGameState.activePlayer))
      .map(_.gameObject.objectId)
      .toSeq
    (tappedPermanents.map(UntapObjectEvent), None)
  }
}
