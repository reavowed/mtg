package mtg.game.turns.priority

import mtg.game.state.{InternalGameActionResult, GameState, InternalGameAction}
import mtg.sbas.LethalDamageStateBasedAction

object StateBasedActionCheck extends InternalGameAction {
  val allStateBasedActions = Seq(LethalDamageStateBasedAction)

  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val actions = allStateBasedActions.flatMap(_.getApplicableEvents(currentGameState))
    if (actions.nonEmpty) {
      actions :+ StateBasedActionCheck
    } else {
      Nil
    }
  }
}
