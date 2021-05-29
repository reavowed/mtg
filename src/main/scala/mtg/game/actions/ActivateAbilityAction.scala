package mtg.game.actions

import mtg.abilities.ActivatedAbilityDefinition
import mtg.game.{ObjectId, PlayerId}
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameActionResult, ObjectWithState}

case class ActivateAbilityAction(player: PlayerId, source: ObjectWithState, ability: ActivatedAbilityDefinition, abilityIndex: Int) extends PriorityAction {
  override def objectId: ObjectId = source.gameObject.objectId
  override def displayText: String = ability.getText(source.characteristics.name)
  override def optionText: String = "Activate " + source.gameObject.objectId + " " + source.characteristics.abilities.indexOf(ability)

  override def execute(currentGameState: GameState): InternalGameActionResult = {
    ability.costs.flatMap(_.payForAbility(source)) :+ ResolveActivatedAbility(player, source, ability)
  }
}

object ActivateAbilityAction {
  def getActivatableAbilities(player: PlayerId, gameState: GameState): Seq[ActivateAbilityAction] = {
    gameState.gameObjectState.derivedState.allObjectStates.values.view
      .flatMap { source =>
        source.characteristics.abilities.ofType[ActivatedAbilityDefinition].zipWithIndex.map { case (a, i) => ActivateAbilityAction(player, source, a, i) }
      }
      .filter(action => canActivateAbility(player, action.source, action.ability))
      .toSeq
  }
  private def canActivateAbility(player: PlayerId, source: ObjectWithState, ability: ActivatedAbilityDefinition): Boolean = {
    ability.functionalZones.contains(source.gameObject.zone.zoneType) &&
      source.controllerOrOwner == player &&
      !ability.costs.exists(_.isUnpayable(source))
  }
}
