package mtg.game.priority

import mtg.abilities.PendingTriggeredAbility
import mtg.actions.stack.CreateTriggeredAbilityOnStack
import mtg.core.PlayerId
import mtg.game.state._
import mtg.stack.adding.{ChooseModes, ChooseTargets, FinishTriggering}

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
        putTriggeredAbilityOnStack(triggeredAbility).andThen(putTriggeredAbilitiesOnStackForPlayer(player))
      case None =>
        ()
    }
  }

  def putTriggeredAbilityOnStack(pendingTriggeredAbility: PendingTriggeredAbility): GameAction[Unit] = {
    for {
      stackObjectId <- CreateTriggeredAbilityOnStack(pendingTriggeredAbility).map(_.get)
      _ <- ChooseModes(stackObjectId)
      _ <- ChooseTargets(stackObjectId)
      _ <- FinishTriggering(stackObjectId)
    } yield ()
  }

  private def chooseTriggeredAbility(player: PlayerId): GameAction[Option[PendingTriggeredAbility]] = {
    getTriggeredAbilitiesForPlayer(player).flatMap(chooseTriggeredAbility(player, _))
  }
  def getTriggeredAbilitiesForPlayer(player: PlayerId): GameAction[Seq[PendingTriggeredAbility]] = {
    CalculatedGameAction(_.gameObjectState.triggeredAbilitiesWaitingToBePutOnStack.filter(_.triggeredAbility.ownerId == player))
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

case class TriggeredAbilityChoice(playerToAct: PlayerId, abilities: Seq[PendingTriggeredAbility]) extends Choice[PendingTriggeredAbility] {
  override def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[PendingTriggeredAbility] = {
    serializedDecision.toIntOption.flatMap(id => abilities.find(_.id == id))
  }
}
