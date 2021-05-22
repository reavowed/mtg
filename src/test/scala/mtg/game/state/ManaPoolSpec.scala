package mtg.game.state

import mtg.SpecWithGameStateManager
import mtg.data.cards.Plains
import mtg.data.sets.Strixhaven
import mtg.game.actions.ActivateAbilityAction
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.{PriorityChoice, StartNextTurnAction}

class ManaPoolSpec extends SpecWithGameStateManager {
  "mana pool" should {
    "empty at the end of a step" in {
      val plains = Strixhaven.cardPrintingsByDefinition(Plains)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(Map(playerOne -> Seq(plains)))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      val ability = manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction].head
      manager.handleDecision(ability.optionText, playerOne)

      manager.currentGameState.gameObjectState.manaPools(playerOne) must not(beEmpty)

      manager.passPriority(playerOne)
      manager.passPriority(playerTwo)

      manager.currentGameState.gameObjectState.manaPools(playerOne) must beEmpty
    }

    "empty at the end of a phase" in {
      val plains = Strixhaven.cardPrintingsByDefinition(Plains)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setBattlefield(Map(playerOne -> Seq(plains)))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      val ability = manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction].head
      manager.handleDecision(ability.optionText, playerOne)

      manager.currentGameState.gameObjectState.manaPools(playerOne) must not(beEmpty)

      manager.passPriority(playerOne)
      manager.passPriority(playerTwo)

      manager.currentGameState.gameObjectState.manaPools(playerOne) must beEmpty
    }
  }

}
