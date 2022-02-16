package mtg.stack.adding

import mtg.abilities.ActivatedAbilityDefinition
import mtg.core.ObjectId
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.{ExecutableGameAction, GameState, PartialGameActionResult}

case class PayCosts(stackObjectId: ObjectId) extends ExecutableGameAction[Any] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    val ability = stackObjectWithState.gameObject.underlyingObject.asInstanceOf[AbilityOnTheStack]
    val source = gameState.gameObjectState.derivedState.allObjectStates(ability.source)
    val costs = ability.abilityDefinition.asInstanceOf[ActivatedAbilityDefinition].costs
    PartialGameActionResult.children(costs.map(_.payForAbility(source)): _*)
  }
}
