package mtg.helpers

import mtg.abilities.ActivatedAbilityDefinition
import mtg.cards.CardDefinition
import mtg.game.objects.{AbilityOnTheStack, StackObject}
import mtg.game.state.GameStateManager
import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecificationLike

trait StackObjectHelpers extends SpecificationLike with GameStateManagerHelpers {
  def beActivatedAbilityOf(
    cardDefinition: CardDefinition)(
    implicit gameStateManager: GameStateManager
  ): Matcher[StackObject] = {
    val expectedAbility = gameStateManager.getPermanent(cardDefinition)
      .currentState(gameStateManager.gameState)
      .characteristics.abilities
      .ofType[ActivatedAbilityDefinition]
      .single
    ((_: StackObject).underlyingObject.asOptionalInstanceOf[AbilityOnTheStack]) ^^ beSome(
      ((_: AbilityOnTheStack).abilityDefinition) ^^ beEqualTo(expectedAbility)
    )
  }
}
