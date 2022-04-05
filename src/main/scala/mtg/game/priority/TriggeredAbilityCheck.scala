package mtg.game.priority

import mtg.abilities.PendingTriggeredAbility
import mtg.core.PlayerId
import mtg.game.objects.StackObject
import mtg.game.state._
import mtg.stack.adding.{ChooseModes, ChooseTargets, FinishTriggering, GetMostRecentStackObjectId}

object TriggeredAbilityCheck extends DelegatingGameAction[Boolean] {
  override def delegate(implicit gameState: GameState): GameAction[Boolean] = {
    if (gameState.gameObjectState.triggeredAbilitiesWaitingToBePutOnStack.nonEmpty) {
      gameState.playersInApnapOrder
        .map(putTriggeredAbilitiesOnStackForPlayer)
        .traverse
        .map(_ => true)
    } else {
      false
    }
  }

  private def putTriggeredAbilitiesOnStackForPlayer(player: PlayerId): GameAction[Unit] = {
    chooseTriggeredAbility(player) flatMap {
      case Some(triggeredAbility) =>
        PutTriggeredAbilityOnStack(triggeredAbility).andThen(putTriggeredAbilitiesOnStackForPlayer(player))
      case None =>
        ()
    }
  }

  private def chooseTriggeredAbility(player: PlayerId): GameAction[Option[PendingTriggeredAbility]] = {
    GetTriggeredAbilitiesForPlayer(player).flatMap(chooseTriggeredAbility(player, _))
  }

  private def chooseTriggeredAbility(player: PlayerId, triggeredAbilities: Seq[PendingTriggeredAbility]): GameAction[Option[PendingTriggeredAbility]] = {
    if (triggeredAbilities.length > 1) {
      TriggeredAbilityChoice(player, triggeredAbilities).map(Some(_))
    } else if (triggeredAbilities.length == 1) {
      Some(triggeredAbilities.head)
    } else {
      None
    }
  }

}

case class GetTriggeredAbilitiesForPlayer(player: PlayerId) extends DelegatingGameAction[Seq[PendingTriggeredAbility]] {
  override def delegate(implicit gameState: GameState): GameAction[Seq[PendingTriggeredAbility]] = {
    gameState.gameObjectState.triggeredAbilitiesWaitingToBePutOnStack.filter(_.triggeredAbility.ownerId == player)
  }
}

case class TriggeredAbilityChoice(playerToAct: PlayerId, abilities: Seq[PendingTriggeredAbility]) extends Choice[PendingTriggeredAbility] {
  override def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[PendingTriggeredAbility] = {
    serializedDecision.toIntOption.flatMap(id => abilities.find(_.id == id))
  }
}

case class PutTriggeredAbilityOnStack(pendingTriggeredAbility: PendingTriggeredAbility) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    for {
      _ <- CreateTriggeredAbilityOnStack(pendingTriggeredAbility)
      // TODO: CreateTriggeredAbilityOnStack should return ID
      stackObjectId <- GetMostRecentStackObjectId
      _ <- ChooseModes(stackObjectId)
      _ <- ChooseTargets(stackObjectId)
      _ <- FinishTriggering(stackObjectId)
    } yield ()
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
