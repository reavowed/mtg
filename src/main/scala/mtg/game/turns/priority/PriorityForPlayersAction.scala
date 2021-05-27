package mtg.game.turns.priority

import mtg.game.PlayerIdentifier
import mtg.game.actions.PriorityAction
import mtg.game.state.history.LogEvent
import mtg.game.state.{BackupAction, GameAction, GameState, InternalGameAction}
import mtg.sbas.StateBasedActionCheck

case class PriorityForPlayersAction(players: Seq[PlayerIdentifier]) extends InternalGameAction {
  override def execute(gameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    players match {
      case playerToAct +: remainingPlayers =>
        val backupAction = BackupAction(gameState.addActions(Seq(this)))
        val priorityChoice = PriorityChoice(
          playerToAct,
          remainingPlayers,
          PriorityAction.getAll(playerToAct, gameState, backupAction))
        (Seq(StateBasedActionCheck, priorityChoice), None)
      case Nil =>
        (Nil, None)
    }
  }
}
