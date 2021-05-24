package mtg.game.state

import mtg.SpecWithGameStateManager
import mtg.data.cards.Plains
import mtg.data.sets.Strixhaven
import mtg.game.actions.ActivateAbilityAction
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.StartNextTurnAction
import mtg.game.turns.priority.PriorityChoice

class ManaPoolSpec extends SpecWithGameStateManager {
  "mana pool" should {
    "empty at the end of a step" in {
      val initialState = emptyGameObjectState.setBattlefield(playerOne, Seq(Plains))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.activateAbility(playerOne, Plains)

      manager.currentGameState.gameObjectState.manaPools(playerOne) must not(beEmpty)

      manager.passPriority(playerOne)
      manager.passPriority(playerTwo)

      manager.currentGameState.gameObjectState.manaPools(playerOne) must beEmpty
    }

    "empty at the end of a phase" in {
      val initialState = emptyGameObjectState.setBattlefield(playerOne, Seq(Plains))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbility(playerOne, Plains)

      manager.currentGameState.gameObjectState.manaPools(playerOne) must not(beEmpty)

      manager.passPriority(playerOne)
      manager.passPriority(playerTwo)

      manager.currentGameState.gameObjectState.manaPools(playerOne) must beEmpty
    }
  }

}
