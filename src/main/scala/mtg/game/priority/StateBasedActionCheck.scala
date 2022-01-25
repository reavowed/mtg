package mtg.game.priority

import mtg.game.state.{ExecutableGameAction, GameState, PartialGameActionResult, WrappedOldUpdates}
import mtg.sbas.LethalDamageStateBasedAction

object StateBasedActionCheck extends ExecutableGameAction[Boolean] {
  val allStateBasedActions = Seq(LethalDamageStateBasedAction)

  override def execute()(implicit gameState: GameState): PartialGameActionResult[Boolean] = {
    val actions = allStateBasedActions.flatMap(_.getApplicableEvents(gameState))
    if (actions.nonEmpty) {
      // TODO: Ensure actions executed simultaneously
      PartialGameActionResult.childThenValue(
        WrappedOldUpdates(actions: _*),
        true)
    } else {
      PartialGameActionResult.Value(false)
    }
  }
}
