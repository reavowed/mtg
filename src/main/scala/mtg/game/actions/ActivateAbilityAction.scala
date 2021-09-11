package mtg.game.actions

import mtg.abilities.ActivatedAbilityDefinition
import mtg.game.stack.ResolveManaAbility
import mtg.game.{ObjectId, PlayerId}
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, InternalGameActionResult, GameState, ObjectWithState}

case class ActivateAbilityAction(player: PlayerId, objectWithAbility: ObjectWithState, ability: ActivatedAbilityDefinition, abilityIndex: Int) extends PriorityAction {
  override def objectId: ObjectId = objectWithAbility.gameObject.objectId
  override def displayText: String = ability.getText(objectWithAbility.characteristics.name.getOrElse("this object"))
  override def optionText: String = "Activate " + objectWithAbility.gameObject.objectId + " " + objectWithAbility.characteristics.abilities.indexOf(ability)

  override def execute(currentGameState: GameState): InternalGameActionResult = {
    ability.costs.flatMap(_.payForAbility(objectWithAbility)) :+ ResolveManaAbility(player, objectWithAbility, ability)
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
