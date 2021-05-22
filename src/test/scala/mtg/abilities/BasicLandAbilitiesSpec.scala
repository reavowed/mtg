package mtg.abilities

import mtg.SpecWithGameStateManager
import mtg.characteristics.Color
import mtg.data.cards.Plains
import mtg.data.sets.Strixhaven
import mtg.effects.AddManaEffect
import mtg.game.actions.ActivateAbilityAction
import mtg.game.objects.{CardObject, GameObject}
import mtg.game.state.{ObjectWithState, PermanentStatus}
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.StartNextTurnAction
import mtg.game.turns.priority.PriorityChoice
import mtg.parts.costs.TapSymbol
import org.specs2.matcher.Matcher

class BasicLandAbilitiesSpec extends SpecWithGameStateManager {
  def beActivatableAbilityAction(gameObject: GameObject, abilityDefinition: ActivatedAbilityDefinition): Matcher[ActivateAbilityAction] = { (action: ActivateAbilityAction) =>
    (action.source.gameObject == gameObject && action.ability == abilityDefinition, "", "")
  }

  def isObjectWithAbility(gameObject: GameObject, abilityDefinition: AbilityDefinition): Matcher[ObjectWithState] = { (objectWithState : ObjectWithState) =>
    (objectWithState.gameObject == gameObject && objectWithState.characteristics.abilities.contains(abilityDefinition), "", "")
  }
  "basic land cards" should {
    "have an appropriate mana ability" in {
      val plains = Strixhaven.cardPrintingsByDefinition(Plains)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(Map(playerOne -> Seq(plains)))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      val plainsObject = manager.currentGameState.gameObjectState.battlefield.find(_.asOptionalInstanceOf[CardObject].exists(_.card.printing == plains)).get
      manager.currentGameState.derivedState.allObjectStates must contain(isObjectWithAbility(plainsObject, ActivatedAbilityDefinition(Seq(TapSymbol), Seq(AddManaEffect(Color.White)))))
    }

    "be tappable for mana by their controller" in {
      val plains = Strixhaven.cardPrintingsByDefinition(Plains)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(Map(playerOne -> Seq(plains)))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      val plainsObject = manager.currentGameState.gameObjectState.battlefield.getCard(plains)
      val plainsState = manager.currentGameState.derivedState.objectStates(plainsObject.objectId)
      manager.currentGameState.pendingActions.head should bePriorityForPlayer(playerOne)
      manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction] must contain(exactly(
        beActivatableAbilityAction(plainsObject, plainsState.characteristics.abilities.head.asInstanceOf[ActivatedAbilityDefinition])))
    }

    "not be tappable for mana by a player who doesn't control them" in {
      val plains = Strixhaven.cardPrintingsByDefinition(Plains)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(Map(playerOne -> Seq(plains)))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.passPriority(playerOne)

      manager.currentGameState.pendingActions.head should bePriorityForPlayer(playerTwo)
      manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction] must beEmpty
    }

    "tap for mana" in {
      val plains = Strixhaven.cardPrintingsByDefinition(Plains)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(Map(playerOne -> Seq(plains)))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      val plainsObject = manager.currentGameState.gameObjectState.battlefield.getCard(plains)
      val ability = manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction].head
      manager.handleDecision(ability.optionText, playerOne)

      manager.currentGameState.pendingActions.head should bePriorityForPlayer(playerOne)

      val plainsState = manager.currentGameState.derivedState.objectStates(plainsObject.objectId)
      plainsState.gameObject.permanentStatus must beSome(PermanentStatus(isTapped = true, isFlipped = false, isFaceDown = false, isPhasedOut = false))

      manager.currentGameState.gameObjectState.manaPools(playerOne).map(_.manaType) must contain(exactly(Color.White.manaType))
    }

    "not tap for mana twice" in {
      val plains = Strixhaven.cardPrintingsByDefinition(Plains)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(Map(playerOne -> Seq(plains)))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      val ability = manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction].head
      manager.handleDecision(ability.optionText, playerOne)

      manager.currentGameState.pendingActions.head should bePriorityForPlayer(playerOne)
      manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction] must beEmpty
    }

    "return priority to NAP after tapping for mana" in {
      val plains = Strixhaven.cardPrintingsByDefinition(Plains)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(Map(playerTwo -> Seq(plains)))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.passPriority(playerOne)

      val ability = manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction].head
      manager.handleDecision(ability.optionText, playerTwo)

      manager.currentGameState.pendingActions.head should bePriorityForPlayer(playerTwo)
    }
  }
}
