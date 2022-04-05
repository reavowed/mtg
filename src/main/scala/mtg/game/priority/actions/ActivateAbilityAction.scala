package mtg.game.priority.actions

import mtg.abilities.ActivatedAbilityDefinition
import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects.{AbilityOnTheStack, StackObject}
import mtg.game.state._
import mtg.stack.adding._
import mtg.stack.resolving.ResolveManaAbility

case class ActivateAbilityAction(player: PlayerId, objectWithAbility: ObjectWithState, ability: ActivatedAbilityDefinition, abilityIndex: Int) extends PriorityAction {
  override def objectId: ObjectId = objectWithAbility.gameObject.objectId
  override def displayText: String = ability.getText(objectWithAbility.characteristics.name.getOrElse("this object"))
  override def optionText: String = "Activate " + objectWithAbility.gameObject.objectId + " " + objectWithAbility.characteristics.abilities.indexOf(ability)

  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    if (ability.isManaAbility) {
      for {
        _ <- ability.costs.map(_.payForAbility(objectWithAbility)).traverse
        _ <- ResolveManaAbility(player, objectWithAbility, ability)
      } yield ()
    } else {
      for {
        _ <- CreateActivatedAbilityOnStack(ability, objectId, player)
        // TODO: CreateTriggeredAbilityOnStack should return ID
        stackObjectId <- GetMostRecentStackObjectId
        _ <- ChooseModes(stackObjectId)
        _ <- ChooseTargets(stackObjectId)
        _ <- PayCosts(stackObjectId)
        _ <- FinishActivating(stackObjectId)
      } yield ()
    }
  }
}

object ActivateAbilityAction {
  def getActivatableAbilities(player: PlayerId, gameState: GameState): Seq[ActivateAbilityAction] = {
    gameState.gameObjectState.derivedState.allObjectStates.values.view
      .flatMap { objectWithState =>
        objectWithState.characteristics.abilities.ofType[ActivatedAbilityDefinition].zipWithIndex.map { case (a, i) => ActivateAbilityAction(player, objectWithState, a, i) }
      }
      .filter(action => canActivateAbility(player, action.objectWithAbility, action.ability))
      .toSeq
  }
  private def canActivateAbility(player: PlayerId, objectWithAbility: ObjectWithState, ability: ActivatedAbilityDefinition): Boolean = {
    ability.isFunctional(objectWithAbility) &&
      objectWithAbility.controllerOrOwner == player &&
      !ability.costs.exists(_.isUnpayable(objectWithAbility))
  }
  def matchDecision(serializedDecision: String, availableActions: Seq[ActivateAbilityAction]): Option[ActivateAbilityAction] = {
    if (serializedDecision.startsWith("Activate ")) {
      serializedDecision.substring("Activate ".length).split(" ").toSeq match {
        case Seq(objectId, index) =>
          availableActions.find(a => a.objectWithAbility.gameObject.objectId.toString == objectId && a.abilityIndex.toString == index)
        case _ =>
          None
      }
    } else None
  }
}

case class CreateActivatedAbilityOnStack(ability: ActivatedAbilityDefinition, sourceId: ObjectId, controller: PlayerId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.addObjectToStack(StackObject(AbilityOnTheStack(ability, sourceId, controller), _, controller))
  }
  override def canBeReverted: Boolean = true
}
