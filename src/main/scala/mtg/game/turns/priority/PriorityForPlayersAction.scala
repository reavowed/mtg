package mtg.game.turns.priority

import mtg.game.PlayerId
import mtg.game.actions.PriorityAction
import mtg.game.state.{BackupAction, GameState, InternalGameAction, GameActionResult}
import mtg.sbas.StateBasedActionCheck

case class PriorityForPlayersAction(players: Seq[PlayerId]) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    players match {
      case playerToAct +: remainingPlayers =>
        val backupAction = BackupAction(gameState.addActions(Seq(this)))
        val priorityChoice = PriorityChoice(
          playerToAct,
          remainingPlayers,
          PriorityAction.getAll(playerToAct, gameState, backupAction))
        Seq(StateBasedActionCheck, priorityChoice)
      case Nil =>
        ()
    }
  }
}
