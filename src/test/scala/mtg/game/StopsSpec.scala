package mtg.game

import mtg.SpecWithGameStateManager
import mtg.definitions.zones.Zone
import mtg.game.state.{GameStateManager, Stops}
import mtg.game.turns.TurnPhase.{CombatPhase, PostcombatMainPhase, PrecombatMainPhase}
import mtg.game.turns.TurnStep.BeginningOfCombatStep
import mtg.game.turns.turnEvents.ExecuteTurn
import mtg.game.turns.{Turn, TurnPhase, TurnStep}
import mtg.sets.alpha.cards.{LightningBolt, Mountain}

class StopsSpec extends SpecWithGameStateManager {
  "stops" should {
    "stop each player in main phases by default" in {
      val manager = new GameStateManager(
        createGameState(gameObjectStateWithInitialLibrariesAndHands, ExecuteTurn(Turn(1, playerOne))),
        _ => {},
        Stops.default(players))

      manager.gameState.currentPhase must beSome[TurnPhase](PrecombatMainPhase)
      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne))

      manager.passPriority(playerOne)

      manager.gameState.currentPhase must beSome[TurnPhase](PostcombatMainPhase)
      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne))

      manager.passPriority(playerOne)

      manager.gameState.currentPhase must beSome[TurnPhase](PrecombatMainPhase)
      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerTwo))
    }

    "stop a player in a step if stop is manually set" in {
      val manager = new GameStateManager(
        createGameState(gameObjectStateWithInitialLibrariesAndHands, ExecuteTurn(Turn(1, playerOne))),
        _ => {},
        Stops.default(players))

      manager.setStop(playerTwo, playerOne, BeginningOfCombatStep)
      manager.passPriority(playerOne)

      manager.gameState.currentPhase must beSome[TurnPhase](CombatPhase)
      manager.gameState.currentStep must beSome[TurnStep](BeginningOfCombatStep)
      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerTwo))
    }

    "give a player priority in response to a spell" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Mountain)
        .setHand(playerOne, LightningBolt)

      val manager = new GameStateManager(
        createGameState(initialState, ExecuteTurn(Turn(1, playerOne))),
        _ => {},
        Stops.default(players))

      manager.setStop(playerTwo, playerOne, BeginningOfCombatStep)
      manager.activateAbility(playerOne, Mountain)
      manager.castSpell(playerOne, LightningBolt)
      manager.choosePlayer(playerOne, playerTwo)
      manager.passPriority(playerOne)

      manager.gameState.currentPhase must beSome[TurnPhase](PrecombatMainPhase)
      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerTwo))
      Zone.Stack(manager.gameState) must contain(beCardObject(LightningBolt))
    }
  }
}
