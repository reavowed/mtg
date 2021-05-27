package mtg.game.actions

import mtg.abilities.ActivatedAbilityDefinition
import mtg.game.PlayerIdentifier
import mtg.game.objects.ObjectId
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameActionResult, ObjectWithState}

case class ActivateAbilityAction(player: PlayerIdentifier, source: ObjectWithState, ability: ActivatedAbilityDefinition, abilityIndex: Int) extends PriorityAction {
  override def objectId: ObjectId = source.gameObject.objectId
  override def displayText: String = ability.text
  override def optionText: String = "Activate " + source.gameObject.objectId + " " + source.characteristics.abilities.indexOf(ability)

  override def execute(currentGameState: GameState): InternalGameActionResult = {
    ability.costs.flatMap(_.payForAbility(source)) ++ ability.effects.flatMap(_.resolveForAbility(player))
  }
}

object ActivateAbilityAction {
  def getActivatableAbilities(player: PlayerIdentifier, gameState: GameState): Seq[ActivateAbilityAction] = {
    gameState.derivedState.allObjectStates
      .flatMap { source =>
        source.characteristics.abilities.ofType[ActivatedAbilityDefinition].zipWithIndex.map { case (a, i) => ActivateAbilityAction(player, source, a, i) }
      }
      .filter(action => canActivateAbility(player, action.source, action.ability))
  }
  private def canActivateAbility(player: PlayerIdentifier, source: ObjectWithState, ability: ActivatedAbilityDefinition): Boolean = {
    ability.functionalZones.contains(source.gameObject.zone.zoneType) &&
      source.controller.getOrElse(source.gameObject.owner) == player &&
      !ability.costs.exists(_.isUnpayable(source))
  }
}
