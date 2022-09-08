package mtg.sets.strixhaven.cards

import mtg.SpecWithGameStateManager
import mtg.definitions.zones.Zone
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.instructions.verbs.SearchLibraryChoice
import mtg.sets.alpha.cards.{Forest, Island, Plains}
import mtg.stack.resolving.ResolveInstructionChoice

class EnvironmentalSciencesSpec extends SpecWithGameStateManager {
  "Environmental Sciences" should {
    "be castable from hand" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Forest, 2)
        .setHand(playerOne, EnvironmentalSciences)
        .setLibrary(playerOne, Island, Plains, AgelessGuardian)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne).withAvailableSpell(EnvironmentalSciences))
    }

    "offer the choice of a basic land" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Forest, 2)
        .setHand(playerOne, EnvironmentalSciences)
        .setLibrary(playerOne, Island, Plains, AgelessGuardian)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 2)
      manager.castSpell(playerOne, EnvironmentalSciences)
      manager.resolveNext()

      manager.currentChoice must beSome(beAnInstanceOf[ResolveInstructionChoice])
      manager.currentChoice.get.asInstanceOf[ResolveInstructionChoice].instructionChoice.playerChoosing mustEqual playerOne
      manager.currentChoice.get.asInstanceOf[ResolveInstructionChoice].instructionChoice must beAnInstanceOf[SearchLibraryChoice]
      manager.currentChoice.get.asInstanceOf[ResolveInstructionChoice].instructionChoice.asInstanceOf[SearchLibraryChoice].possibleChoices mustEqual manager.getCards(Island, Plains).map(_.objectId)
    }

    "put the chosen land into its controller's hand" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Forest, 2)
        .setHand(playerOne, EnvironmentalSciences)
        .setLibrary(playerOne, Island, Plains, AgelessGuardian)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 2)
      manager.castSpell(playerOne, EnvironmentalSciences)
      manager.resolveNext()
      manager.chooseCard(playerOne, Island)

      playerOne.hand(manager.gameState) must contain(exactly(beCardObject(Island)))
    }

    "shuffle its controller's library" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Forest, 2)
        .setHand(playerOne, EnvironmentalSciences)
        .setLibrary(playerOne, Island, Plains, AgelessGuardian)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 2)
      manager.castSpell(playerOne, EnvironmentalSciences)
      manager.resolveNext()
      manager.chooseCard(playerOne, Island)

      val finalState = manager.gameState
      playerOne.library(finalState).map(_.objectId) must not(contain(anyOf(playerOne.library(initialState).map(_.objectId): _*)))
    }

    "gain its controller two life" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Forest, 2)
        .setHand(playerOne, EnvironmentalSciences)
        .setLibrary(playerOne, Island, Plains, AgelessGuardian)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 2)
      manager.castSpell(playerOne, EnvironmentalSciences)
      manager.resolveNext()
      manager.chooseCard(playerOne, Island)

      manager.gameState.gameObjectState.lifeTotals(playerOne) mustEqual 22
    }

    "not allow choosing a creature" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Forest, 2)
        .setHand(playerOne, EnvironmentalSciences)
        .setLibrary(playerOne, Island, Plains, AgelessGuardian)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 2)
      manager.castSpell(playerOne, EnvironmentalSciences)
      manager.resolveNext()

      val stateBeforeChoice = manager.gameState
      manager.chooseCard(playerOne, AgelessGuardian)

      manager.gameState mustEqual stateBeforeChoice
    }

    "go to graveyard after resolution" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Forest, 2)
        .setHand(playerOne, EnvironmentalSciences)
        .setLibrary(playerOne, Island, Plains, AgelessGuardian)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 2)
      manager.castSpell(playerOne, EnvironmentalSciences)
      manager.resolveNext()
      manager.chooseCard(playerOne, Island)

      Zone.Stack(manager.gameState) must not(contain(beCardObject(EnvironmentalSciences)))
      playerOne.graveyard(manager.gameState) must contain(beCardObject(EnvironmentalSciences))
    }

    "have correct oracle text" in {
      EnvironmentalSciences.text mustEqual "Search your library for a basic land card, reveal it, put it into your hand, then shuffle. You gain 2 life."
    }

    // TODO: not allow a non-basic land?
  }

}
