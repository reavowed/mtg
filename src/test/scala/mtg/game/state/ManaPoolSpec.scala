package mtg.game.state

import mtg.SpecWithGameStateManager
import mtg.data.cards.Plains
import mtg.game.turns.TurnPhase.PrecombatMainPhase

class ManaPoolSpec extends SpecWithGameStateManager {
  "mana pool" should {
    "empty at the end of a step" in {
      val initialState = emptyGameObjectState.setBattlefield(playerOne, Seq(Plains))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.activateAbility(playerOne, Plains)

      manager.gameState.gameObjectState.manaPools(playerOne) must not(beEmpty)

      manager.passPriority(playerOne)
      manager.passPriority(playerTwo)

      manager.gameState.gameObjectState.manaPools(playerOne) must beEmpty
    }

    "empty at the end of a phase" in {
      val initialState = emptyGameObjectState.setBattlefield(playerOne, Seq(Plains))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbility(playerOne, Plains)

      manager.gameState.gameObjectState.manaPools(playerOne) must not(beEmpty)

      manager.passPriority(playerOne)
      manager.passPriority(playerTwo)

      manager.gameState.gameObjectState.manaPools(playerOne) must beEmpty
    }
  }

}
