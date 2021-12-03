package mtg.abilities

import mtg.SpecWithGameStateManager
import mtg.characteristics.Color
import mtg.data.cards.Plains
import mtg.effects.oneshot
import mtg.effects.oneshot.basic
import mtg.effects.oneshot.basic.AddManaEffect
import mtg.game.Zone
import mtg.game.actions.ActivateAbilityAction
import mtg.game.objects.GameObject
import mtg.game.state.ObjectWithState
import mtg.game.turns.StartNextTurnAction
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.parts.costs.TapSymbol
import org.specs2.matcher.Matcher

class BasicLandAbilitiesSpec extends SpecWithGameStateManager {
  def beActivatableAbilityAction(gameObject: GameObject, abilityDefinition: ActivatedAbilityDefinition): Matcher[ActivateAbilityAction] = { (action: ActivateAbilityAction) =>
    (action.objectWithAbility.gameObject == gameObject && action.ability == abilityDefinition, "", "")
  }

  def isObjectWithAbility(gameObject: GameObject, abilityDefinition: AbilityDefinition): Matcher[ObjectWithState] = { (objectWithState : ObjectWithState) =>
    (objectWithState.gameObject == gameObject && objectWithState.characteristics.abilities.contains(abilityDefinition), "", "")
  }
  "basic land cards" should {
    "have an appropriate mana ability" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(playerOne, Seq(Plains))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      val plainsObject = manager.getCard(Zone.Battlefield, Plains)
      val plainsState = manager.getState(plainsObject)
      plainsState.characteristics.abilities must contain(ActivatedAbilityDefinition(Seq(TapSymbol), basic.AddManaEffect(Color.White)))
    }

    "be tappable for mana by their controller" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(playerOne, Seq(Plains))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      val plainsObject = manager.getCard(Zone.Battlefield, Plains)
      val plainsState = manager.getState(plainsObject)
      manager.currentChoice should beSome(bePriorityChoice.forPlayer(playerOne)
        .withAvailableAbility(beActivatableAbilityAction(plainsObject, plainsState.characteristics.abilities.head.asInstanceOf[ActivatedAbilityDefinition])))
    }

    "not be tappable for mana by a player who doesn't control them" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(playerOne, Seq(Plains))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.passPriority(playerOne)

      manager.currentChoice should beSome(bePriorityChoice.forPlayer(playerTwo).withAvailableAbilities(beEmpty))
    }

    "tap for mana" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(playerOne, Seq(Plains))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbility(playerOne, Plains)

      manager.currentChoice should beSome(bePriorityChoice.forPlayer(playerOne))
      manager.getCard(Zone.Battlefield, Plains) must beTapped
      manager.gameState.gameObjectState.manaPools(playerOne).map(_.manaType) must contain(exactly(Color.White.manaType))
    }

    "not tap for mana twice" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(playerOne, Seq(Plains))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbility(playerOne, Plains)

      manager.currentChoice should beSome(bePriorityChoice.forPlayer(playerOne).withAvailableAbilities(beEmpty))
    }

    "return priority to NAP after tapping for mana" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(playerTwo, Seq(Plains))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.passPriority(playerOne)
      manager.activateAbility(playerTwo, Plains)

      manager.currentChoice should beSome(bePriorityChoice.forPlayer(playerTwo))
    }
  }
}
