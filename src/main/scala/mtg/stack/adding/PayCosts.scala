package mtg.stack.adding

import mtg.abilities.ActivatedAbilityDefinition
import mtg.definitions.ObjectId
import mtg.game.objects.AbilityOnTheStack
import mtg.game.state.{DelegatingGameAction, GameAction, GameState}

case class PayCosts(stackObjectId: ObjectId) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    val ability = stackObjectWithState.gameObject.underlyingObject.asInstanceOf[AbilityOnTheStack]
    val source = gameState.gameObjectState.derivedState.allObjectStates(ability.source)
    val costs = ability.abilityDefinition.asInstanceOf[ActivatedAbilityDefinition].costs
    for {
      _ <- costs.map(_.payForAbility(source)).traverse
    } yield ()
  }
}
