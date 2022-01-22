package mtg.game.priority

import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.sbas.LethalDamageStateBasedAction

object StateBasedActionCheck extends InternalGameAction {
  val allStateBasedActions = Seq(LethalDamageStateBasedAction)

  override def execute(gameState: GameState): GameActionResult = {
    val actions = allStateBasedActions.flatMap(_.getApplicableEvents(gameState))
    if (actions.nonEmpty) {
      actions :+ StateBasedActionCheck
    } else {
      Nil
    }
  }
  override def canBeReverted: Boolean = true
}
