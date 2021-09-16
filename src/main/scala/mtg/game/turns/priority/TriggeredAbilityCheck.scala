package mtg.game.turns.priority

import mtg.abilities.PendingTriggeredAbility
import mtg.game.PlayerId
import mtg.game.objects.StackObject
import mtg.game.state._
import mtg.stack.adding.{ChooseTargets, FinishTriggering}

object TriggeredAbilityCheck extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    if (gameState.gameObjectState.triggeredAbilitiesWaitingToBePutOnStack.nonEmpty) {
      Seq(PutTriggeredAbilitiesOnStack(gameState.gameObjectState.triggeredAbilitiesWaitingToBePutOnStack), StateBasedActionCheck, TriggeredAbilityCheck)
    } else {
      ()
    }
  }
  override def canBeReverted: Boolean = true
}

case class PutTriggeredAbilitiesOnStack(triggeredAbilities: Seq[PendingTriggeredAbility]) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.playersInApnapOrder.mapFind(p => Option(triggeredAbilities.filter(_.triggeredAbility.ownerId == p)).filter(_.nonEmpty).map(p -> _))
      .map { case (player, abilities) =>
        if (abilities.length > 1) {
          TriggeredAbilityChoice(player, abilities)
        } else {
          PutTriggeredAbilityOnStack(abilities.head)
        }
      }.toSeq
  }
  override def canBeReverted: Boolean = true
}

case class TriggeredAbilityChoice(playerToAct: PlayerId, abilities: Seq[PendingTriggeredAbility]) extends Choice {
  override def parseDecision(serializedChosenOption: String): Option[Decision] = {
    serializedChosenOption.toIntOption.flatMap(id => abilities.find(_.id == id))
      .map(PutTriggeredAbilityOnStack)
  }
}

case class PutTriggeredAbilityOnStack(pendingTriggeredAbility: PendingTriggeredAbility) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    Seq(
      CreateAbilityOnStack(pendingTriggeredAbility),
      TriggeredAbilitySteps(BackupAction(gameState))
    )
  }
  override def canBeReverted: Boolean = true
}

case class CreateAbilityOnStack(pendingTriggeredAbility: PendingTriggeredAbility) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState
      .removeTriggeredAbility(pendingTriggeredAbility)
      .addNewObject(StackObject(pendingTriggeredAbility.triggeredAbility.toAbilityOnTheStack, _, pendingTriggeredAbility.triggeredAbility.ownerId), _.length)
  }
  override def canBeReverted: Boolean = true
}

case class TriggeredAbilitySteps(backupAction: BackupAction) extends InternalGameAction  {
  override def execute(gameState: GameState): GameActionResult = {
    val stackObjectId = gameState.gameObjectState.stack.last.objectId
    Seq(
      ChooseTargets(stackObjectId, backupAction),
      FinishTriggering(stackObjectId))
  }
  override def canBeReverted: Boolean = true
}
