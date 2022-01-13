package mtg.game.turns.priority

import mtg.game.PlayerId
import mtg.game.state.{BackupAction, ExecutableGameAction, GameState, PartialGameActionResult, WrappedOldUpdates}
import mtg.stack.resolving.ResolveStackObject

case object PrioritySequenceAction extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = executeFromActivePlayer

  private def executeFromActivePlayer(implicit gameState: GameState): PartialGameActionResult[Unit] = executeFromPlayer(gameState.activePlayer)
  private def executeFromPlayer(player: PlayerId)(implicit gameState: GameState): PartialGameActionResult[Unit] = executeForPlayers(gameState.gameData.getPlayersInApNapOrder(player))

  private def executeAfterStackResolution(unit: Unit, gameState: GameState): PartialGameActionResult[Unit] = executeFromActivePlayer(gameState)
  private def executeAfterAction(playerActing: PlayerId)(any: Any, gameState: GameState): PartialGameActionResult[Unit] = executeFromPlayer(playerActing)(gameState)

  private def executeForPlayers(playersLeftToAct: Seq[PlayerId])(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    playersLeftToAct match {
      case playerToAct +: otherPlayers =>
        playerAboutToReceivePriority(playerToAct, otherPlayers)
      case Nil =>
        gameState.gameObjectState.stack match {
          case _ :+ topObject =>
            PartialGameActionResult.ChildWithCallback(WrappedOldUpdates(ResolveStackObject(topObject)), executeAfterStackResolution)
          case Nil =>
            PartialGameActionResult.Value(())
        }
    }
  }

  private def playerAboutToReceivePriority(playerToAct: PlayerId, playersLeftToAct: Seq[PlayerId]): PartialGameActionResult[Unit] = {
    PartialGameActionResult.ChildWithCallback(WrappedOldUpdates(StateBasedActionCheck, TriggeredAbilityCheck), playerGetsPriority(playerToAct, playersLeftToAct))
  }

  private def playerGetsPriority(playerToAct: PlayerId, playersLeftToAct: Seq[PlayerId])(any: Unit, gameState: GameState) : PartialGameActionResult[Unit] = {
    PartialGameActionResult.ChildWithCallback(PriorityChoice(playerToAct)(gameState), handleDecision(playerToAct, playersLeftToAct))
  }

  private def handleDecision(playerToAct: PlayerId, playersLeftToAct: Seq[PlayerId])(decision: PriorityDecision, gameState: GameState) : PartialGameActionResult[Unit] = {
    decision match {
      case PriorityDecision.Pass =>
        executeForPlayers(playersLeftToAct)(gameState)
      case PriorityDecision.TakeAction(action, backupState) =>
        PartialGameActionResult.ChildWithCallback(
          ExecutePriorityAction(action, BackupAction(backupState)),
          executeAfterAction(playerToAct))
    }
  }

}
