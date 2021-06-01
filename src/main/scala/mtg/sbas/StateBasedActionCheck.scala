package mtg.sbas

import mtg.game.state.{GameState, InternalGameAction, GameActionResult}

object StateBasedActionCheck extends InternalGameAction {
  val allStateBasedActions = Seq(LethalDamageStateBasedAction)
  override def execute(currentGameState: GameState): GameActionResult = {
    val actions = allStateBasedActions.flatMap(_.getApplicableEvents(currentGameState))
    if (actions.nonEmpty) {
      actions :+ StateBasedActionCheck
    } else {
      Nil
    }
  }
}
