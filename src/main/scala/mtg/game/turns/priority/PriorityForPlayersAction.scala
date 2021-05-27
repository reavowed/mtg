package mtg.game.turns.priority

import mtg.game.PlayerIdentifier
import mtg.game.actions.PriorityAction
import mtg.game.state.{BackupAction, GameState, InternalGameAction, InternalGameActionResult}
import mtg.sbas.StateBasedActionCheck

case class PriorityForPlayersAction(players: Seq[PlayerIdentifier]) extends InternalGameAction {
  override def execute(gameState: GameState): InternalGameActionResult = {
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
