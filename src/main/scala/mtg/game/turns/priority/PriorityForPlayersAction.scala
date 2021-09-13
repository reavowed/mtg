package mtg.game.turns.priority

import mtg.game.PlayerId
import mtg.game.actions.PriorityAction
import mtg.game.state.{BackupAction, GameState, InternalGameAction, InternalGameActionResult}

case class PriorityForPlayersAction(players: Seq[PlayerId]) extends InternalGameAction {
  override def execute(gameState: GameState): InternalGameActionResult = {
    players match {
      case playerToAct +: remainingPlayers =>
        val backupAction = BackupAction(gameState.addActions(Seq(this)))
        val priorityChoice = PriorityChoice(
          playerToAct,
          remainingPlayers,
          PriorityAction.getAll(playerToAct, gameState, backupAction))
        Seq(StateBasedActionCheck, TriggeredAbilityCheck, priorityChoice)
      case Nil =>
        ()
    }
  }
  override def canBeReverted: Boolean = true
}

object PriorityForPlayersAction {
  def fromActivePlayer(gameState: GameState): PriorityForPlayersAction = {
    PriorityForPlayersAction(gameState.gameData.playersInTurnOrder)
  }
  def fromPlayer(player: PlayerId, gameState: GameState): PriorityForPlayersAction = {
    PriorityForPlayersAction(gameState.gameData.getPlayersInApNapOrder(player))
  }
}
