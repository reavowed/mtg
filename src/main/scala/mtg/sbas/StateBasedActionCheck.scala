package mtg.sbas

import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}

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
