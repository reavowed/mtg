package mtg.game.priority.actions

import mtg.abilities.ActivatedAbilityDefinition
import mtg.game.state.{BackupAction, GameActionResult, GameState, ObjectWithState, PartialGameActionResult, WrappedOldUpdates}
import mtg.game.{ObjectId, PlayerId}
import mtg.stack.resolving.ResolveManaAbility

case class ActivateAbilityAction(player: PlayerId, objectWithAbility: ObjectWithState, ability: ActivatedAbilityDefinition, abilityIndex: Int) extends PriorityAction {
  override def objectId: ObjectId = objectWithAbility.gameObject.objectId
  override def displayText: String = ability.getText(objectWithAbility.characteristics.name.getOrElse("this object"))
  override def optionText: String = "Activate " + objectWithAbility.gameObject.objectId + " " + objectWithAbility.characteristics.abilities.indexOf(ability)

  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    val actions = ability.costs.flatMap(_.payForAbility(objectWithAbility)) :+ ResolveManaAbility(player, objectWithAbility, ability)
    PartialGameActionResult.childThenValue(WrappedOldUpdates(actions: _*), ())
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
