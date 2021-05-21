package mtg.abilities

import mtg.SpecWithGameStateManager
import mtg.characteristics.Color
import mtg.data.cards.Plains
import mtg.data.sets.Strixhaven
import mtg.effects.AddManaEffect
import mtg.game.objects.{CardObject, GameObject}
import mtg.game.state.ObjectWithState
import mtg.game.turns.StartNextTurnAction
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.parts.costs.TapSymbol
import org.specs2.matcher.Matcher

class BasicLandAbilitiesSpec extends SpecWithGameStateManager {
  def isObjectWithAbility(gameObject: GameObject, abilityDefinition: AbilityDefinition): Matcher[ObjectWithState] = { (objectWithState : ObjectWithState) =>
    (objectWithState.gameObject == gameObject && objectWithState.characteristics.abilities.contains(abilityDefinition), "", "")
  }
  "basic land cards" should {
    "have an appropriate mana ability" in {
      val plains = Strixhaven.getCard(Plains).get
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(Map(playerOne -> Seq(plains)))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      val plainsObject = manager.currentGameState.gameObjectState.battlefield.find(_.asOptionalInstanceOf[CardObject].exists(_.card.printing == plains)).get
      manager.currentGameState.derivedState.allObjectStates must contain(isObjectWithAbility(plainsObject, ActivatedAbilityDefinition(Seq(TapSymbol), Seq(AddManaEffect(Color.White)))))
    }
  }
}
