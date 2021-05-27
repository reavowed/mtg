package mtg.sbas

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

object StateBasedActionCheck extends InternalGameAction {
  val allStateBasedActions = Seq(LethalDamageStateBasedAction)
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val actions = allStateBasedActions.flatMap(_.getApplicableEvents(currentGameState))
    if (actions.nonEmpty) {
      (actions :+ StateBasedActionCheck, None)
    } else {
      (Nil, None)
    }
  }
}
