package mtg.game.priority

import mtg.game.state.{DelegatingGameAction, GameAction, GameState, WrappedOldUpdates}
import mtg.sbas.LethalDamageStateBasedAction

object StateBasedActionCheck extends DelegatingGameAction[Boolean] {
  val allStateBasedActions = Seq(LethalDamageStateBasedAction)

  override def delegate(implicit gameState: GameState): GameAction[Boolean] = {
    val actions = allStateBasedActions.flatMap(_.getApplicableEvents(gameState))
    if (actions.nonEmpty) {
      // TODO: Ensure actions executed simultaneously
      WrappedOldUpdates(actions: _*)
        .map(_ => true)
    } else {
      false
    }
  }
}
