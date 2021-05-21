package mtg.game.actions

import mtg.abilities.ActivatedAbilityDefinition
import mtg.game.PlayerIdentifier
import mtg.game.objects.ObjectId
import mtg.game.state.{GameState, ObjectWithState}

case class ActivateAbilityAction(player: PlayerIdentifier, objectWithState: ObjectWithState, ability: ActivatedAbilityDefinition) extends PriorityAction {
  override def objectId: ObjectId = objectWithState.gameObject.objectId
  override def displayText: String = ability.text
  override def optionText: String = "Activate " + objectWithState.gameObject.objectId + " " + objectWithState.characteristics.abilities.indexOf(ability)
}

object ActivateAbilityAction {
  def getActivatableAbilities(player: PlayerIdentifier, gameState: GameState): Seq[ActivateAbilityAction] = {
    gameState.derivedState.allObjectStates
      .flatMap { objectWithState =>
        objectWithState.characteristics.abilities.ofType[ActivatedAbilityDefinition].map(ActivateAbilityAction(player, objectWithState, _))
      }
      .filter(action => canActivateAbility(player, action.objectWithState, action.ability))
  }
  private def canActivateAbility(player: PlayerIdentifier, objectWithState: ObjectWithState, ability: ActivatedAbilityDefinition): Boolean = {
    ability.functionalZones.contains(objectWithState.gameObject.zone.zoneType) &&
      objectWithState.controller.map(_ == player).getOrElse(objectWithState.gameObject.owner == player)
  }
}
