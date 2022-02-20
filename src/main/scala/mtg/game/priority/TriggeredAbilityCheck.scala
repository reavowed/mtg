package mtg.game.priority

import mtg.abilities.PendingTriggeredAbility
import mtg.core.PlayerId
import mtg.game.objects.StackObject
import mtg.game.state._
import mtg.stack.adding.{ChooseModes, ChooseTargets, FinishTriggering}

object TriggeredAbilityCheck extends ExecutableGameAction[Boolean] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Boolean] = {
    if (gameState.gameObjectState.triggeredAbilitiesWaitingToBePutOnStack.nonEmpty) {
      putTriggeredAbilitiesOnStack(gameState.playersInApnapOrder)
    } else {
      PartialGameActionResult.Value(false)
    }
  }

  private def putTriggeredAbilitiesOnStack(players: Seq[PlayerId])(implicit gameState: GameState): PartialGameActionResult[Boolean] = {
    players match {
      case nextPlayer +: remainingPlayers =>
        val triggeredAbilitiesForPlayer = gameState.gameObjectState.triggeredAbilitiesWaitingToBePutOnStack.filter(_.triggeredAbility.ownerId == nextPlayer)
        if (triggeredAbilitiesForPlayer.nonEmpty) {
          putTriggeredAbilitiesOnStackForPlayer(nextPlayer, remainingPlayers, triggeredAbilitiesForPlayer)
        } else {
          putTriggeredAbilitiesOnStack(remainingPlayers)
        }
      case Nil =>
        PartialGameActionResult.Value(true)
    }
  }

  private def putTriggeredAbilitiesOnStackForPlayer(
    player: PlayerId,
    remainingPlayers: Seq[PlayerId],
    triggeredAbilitiesForPlayer: Seq[PendingTriggeredAbility])(
    implicit gameState: GameState
  ): PartialGameActionResult[Boolean] = {
    if (triggeredAbilitiesForPlayer.length > 1) {
      PartialGameActionResult.ChildWithCallback(
        TriggeredAbilityChoice(player, triggeredAbilitiesForPlayer),
        handleTriggeredAbilityChoice(player, remainingPlayers, triggeredAbilitiesForPlayer))
    } else if (triggeredAbilitiesForPlayer.length == 1) {
      handleTriggeredAbilityChoice(player, remainingPlayers, triggeredAbilitiesForPlayer)(triggeredAbilitiesForPlayer.head, gameState)
    } else {
      putTriggeredAbilitiesOnStack(remainingPlayers)
    }
  }

  private def handleTriggeredAbilityChoice(
    player: PlayerId,
    remainingPlayers: Seq[PlayerId],
    triggeredAbilitiesForPlayer: Seq[PendingTriggeredAbility])(
    chosenAbility: PendingTriggeredAbility,
    gameState: GameState
  ): PartialGameActionResult[Boolean] = {
    PartialGameActionResult.ChildWithCallback(
      PutTriggeredAbilityOnStack(chosenAbility),
      (_: Any, gameState) => putTriggeredAbilitiesOnStackForPlayer(player, remainingPlayers, triggeredAbilitiesForPlayer.filter(_ != chosenAbility))(gameState))
  }
}

case class TriggeredAbilityChoice(playerToAct: PlayerId, abilities: Seq[PendingTriggeredAbility]) extends Choice[PendingTriggeredAbility] {
  override def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[PendingTriggeredAbility] = {
    serializedDecision.toIntOption.flatMap(id => abilities.find(_.id == id))
  }
}

case class PutTriggeredAbilityOnStack(pendingTriggeredAbility: PendingTriggeredAbility) extends ExecutableGameAction[Any] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    PartialGameActionResult.ChildWithCallback(
      WrappedOldUpdates(CreateTriggeredAbilityOnStack(pendingTriggeredAbility)),
      steps)
  }

  private def steps(any: Any, gameState: GameState): PartialGameActionResult[Any] = {
    // TODO: Should be result of MoveObjectEvent
    val stackObjectId = gameState.gameObjectState.stack.last.objectId
    PartialGameActionResult.children(
      ChooseModes(stackObjectId),
      ChooseTargets(stackObjectId),
      FinishTriggering(stackObjectId))
  }
}

case class CreateTriggeredAbilityOnStack(pendingTriggeredAbility: PendingTriggeredAbility) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState
      .removeTriggeredAbility(pendingTriggeredAbility)
      .addObjectToStack(StackObject(pendingTriggeredAbility.triggeredAbility.toAbilityOnTheStack, _, pendingTriggeredAbility.triggeredAbility.ownerId))
  }
  override def canBeReverted: Boolean = true
}
