package mtg.game.priority

import mtg.core.PlayerId
import mtg.game.state.{ExecutableGameAction, GameState, PartialGameActionResult, WrappedOldUpdates}
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
        checkStateBasedActionsBeforePriority(playerToAct, otherPlayers)
      case Nil =>
        gameState.gameObjectState.stack match {
          case _ :+ topObject =>
            PartialGameActionResult.ChildWithCallback(ResolveStackObject(topObject), executeAfterStackResolution)
          case Nil =>
            PartialGameActionResult.Value(())
        }
    }
  }

  private def checkStateBasedActionsBeforePriority(playerToAct: PlayerId, playersLeftToAct: Seq[PlayerId]): PartialGameActionResult[Unit] = {
    PartialGameActionResult.ChildWithCallback(
      StateBasedActionCheck,
      handleStateBasedActionsResult(playerToAct, playersLeftToAct))
  }

  private def handleStateBasedActionsResult(playerToAct: PlayerId, playersLeftToAct: Seq[PlayerId])(wereStateBasedActionsExecuted: Boolean, gameState: GameState): PartialGameActionResult[Unit] = {
    if (wereStateBasedActionsExecuted) {
      checkStateBasedActionsBeforePriority(playerToAct, playersLeftToAct)
    } else {
      checkTriggeredAbilitiesBeforePriority(playerToAct, playersLeftToAct)
    }
  }

  private def checkTriggeredAbilitiesBeforePriority(playerToAct: PlayerId, playersLeftToAct: Seq[PlayerId]): PartialGameActionResult[Unit] = {
    PartialGameActionResult.ChildWithCallback(
      TriggeredAbilityCheck,
      handleTriggeredAbilitiesResult(playerToAct, playersLeftToAct))
  }

  private def handleTriggeredAbilitiesResult(playerToAct: PlayerId, playersLeftToAct: Seq[PlayerId])(wereTriggeredAbilitiesPutOnStack: Boolean, gameState: GameState): PartialGameActionResult[Unit] = {
    if (wereTriggeredAbilitiesPutOnStack) {
      checkStateBasedActionsBeforePriority(playerToAct, playersLeftToAct)
    } else {
      PartialGameActionResult.ChildWithCallback(PriorityChoice(playerToAct)(gameState), handleDecision(playerToAct, playersLeftToAct))
    }
  }

  private def handleDecision(playerToAct: PlayerId, playersLeftToAct: Seq[PlayerId])(decision: PriorityDecision, gameState: GameState) : PartialGameActionResult[Unit] = {
    decision match {
      case PriorityDecision.Pass =>
        executeForPlayers(playersLeftToAct)(gameState)
      case PriorityDecision.TakeAction(action) =>
        PartialGameActionResult.ChildWithCallback(
          action,
          executeAfterAction(playerToAct))
    }
  }

}
