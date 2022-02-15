package mtg.game.priority.actions

import mtg.abilities.ActivatedAbilityDefinition
import mtg.game.objects.{AbilityOnTheStack, StackObject}
import mtg.game.state._
import mtg.game.{ObjectId, PlayerId}
import mtg.stack.adding.{ChooseModes, ChooseTargets, FinishActivating, PayCosts}
import mtg.stack.resolving.ResolveManaAbility

case class ActivateAbilityAction(player: PlayerId, objectWithAbility: ObjectWithState, ability: ActivatedAbilityDefinition, abilityIndex: Int) extends PriorityAction {
  override def objectId: ObjectId = objectWithAbility.gameObject.objectId
  override def displayText: String = ability.getText(objectWithAbility.characteristics.name.getOrElse("this object"))
  override def optionText: String = "Activate " + objectWithAbility.gameObject.objectId + " " + objectWithAbility.characteristics.abilities.indexOf(ability)

  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    if (ability.isManaAbility) {
      val actions = ability.costs.map(_.payForAbility(objectWithAbility)) :+ WrappedOldUpdates(ResolveManaAbility(player, objectWithAbility, ability))
      PartialGameActionResult.childrenThenValue(actions, ())
    } else {
      PartialGameActionResult.ChildWithCallback(
        WrappedOldUpdates(CreateActivatedAbilityOnStack(ability, objectId, player)),
        steps)
    }
  }

  private def steps(any: Any, gameState: GameState): PartialGameActionResult[Any] = {
    // TODO: Should be result of MoveObjectEvent
    val stackObjectId = gameState.gameObjectState.stack.last.objectId
    PartialGameActionResult.children(
      ChooseModes(stackObjectId),
      ChooseTargets(stackObjectId),
      PayCosts(stackObjectId),
      FinishActivating(stackObjectId))
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
}

case class CreateActivatedAbilityOnStack(ability: ActivatedAbilityDefinition, sourceId: ObjectId, controller: PlayerId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.addObjectToStack(StackObject(AbilityOnTheStack(ability, sourceId, controller), _, controller))
  }
  override def canBeReverted: Boolean = true
}
