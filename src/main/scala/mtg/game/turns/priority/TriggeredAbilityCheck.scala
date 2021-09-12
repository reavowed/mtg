package mtg.game.turns.priority

import mtg.abilities.PendingTriggeredAbility
import mtg.game.PlayerId
import mtg.game.objects.{AbilityOnTheStack, StackObject}
import mtg.game.stack.steps.{ChooseTargets, FinishTriggering}
import mtg.game.state._

object TriggeredAbilityCheck extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    if (currentGameState.gameObjectState.triggeredAbilitiesWaitingToBePutOnStack.nonEmpty) {
      Seq(PutTriggeredAbilitiesOnStack(currentGameState.gameObjectState.triggeredAbilitiesWaitingToBePutOnStack), StateBasedActionCheck, TriggeredAbilityCheck)
    } else {
      ()
    }
  }
  override def canBeReverted: Boolean = true
}

case class PutTriggeredAbilitiesOnStack(triggeredAbilities: Seq[PendingTriggeredAbility]) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    currentGameState.playersInApnapOrder.mapFind(p => Option(triggeredAbilities.filter(_.triggeredAbility.ownerId == p)).filter(_.nonEmpty).map(p -> _))
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

case class TriggeredAbilityChoice(playerToAct: PlayerId, abilities: Seq[PendingTriggeredAbility]) extends TypedPlayerChoice[PendingTriggeredAbility] {
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[PendingTriggeredAbility] = {
    serializedChosenOption.toIntOption.flatMap(id => abilities.find(_.id == id))
  }
  override def handleDecision(chosenOption: PendingTriggeredAbility, currentGameState: GameState): InternalGameActionResult = {
    PutTriggeredAbilityOnStack(chosenOption)
  }
}

case class PutTriggeredAbilityOnStack(pendingTriggeredAbility: PendingTriggeredAbility) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    Seq(
      CreateAbilityOnStack(pendingTriggeredAbility),
      TriggeredAbilitySteps(BackupAction(currentGameState))
    )
  }
  override def canBeReverted: Boolean = true
}

case class CreateAbilityOnStack(pendingTriggeredAbility: PendingTriggeredAbility) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState
      .removeTriggeredAbility(pendingTriggeredAbility)
      .addNewObject(StackObject(pendingTriggeredAbility.triggeredAbility.toAbilityOnTheStack, _, pendingTriggeredAbility.triggeredAbility.ownerId), _.length)
  }
  override def canBeReverted: Boolean = true
}

case class TriggeredAbilitySteps(backupAction: BackupAction) extends InternalGameAction  {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val stackObjectId = currentGameState.gameObjectState.stack.last.objectId
    Seq(
      ChooseTargets(stackObjectId, backupAction),
      FinishTriggering(stackObjectId))
  }
  override def canBeReverted: Boolean = true
}
