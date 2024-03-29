package mtg.game.turns.turnBasedActions

import mtg.actions.UntapObjectAction
import mtg.definitions.zones.Zone
import mtg.game.state.{DelegatingGameObjectAction, GameObjectAction, GameState}

case object UntapForTurn extends DelegatingGameObjectAction {
  override def delegate(implicit gameState: GameState): Seq[GameObjectAction[_]] = {
    val tappedPermanents = gameState.gameObjectState.derivedState.permanentStates.values.view
      .filter(o => o.gameObject.zone == Zone.Battlefield && o.controller == gameState.activePlayer)
      .map(_.gameObject.objectId)
      .toSeq
    tappedPermanents.map(UntapObjectAction)
  }
}
