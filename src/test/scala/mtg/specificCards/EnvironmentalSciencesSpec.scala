package mtg.specificCards

import mtg.SpecWithGameStateManager
import mtg.data.cards.strixhaven.{AgelessGuardian, EnvironmentalSciences}
import mtg.data.cards.{Forest, Island, Plains}
import mtg.effects.SearchChoice
import mtg.game.Zone
import mtg.game.actions.ResolveEffectChoice
import mtg.game.turns.StartNextTurnAction
import mtg.game.turns.TurnPhase.PrecombatMainPhase

class EnvironmentalSciencesSpec extends SpecWithGameStateManager {
  "Environmental Sciences" should {
    "be castable from hand" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Forest, 2)
        .setHand(playerOne, EnvironmentalSciences)
        .setLibrary(playerOne, Seq(Island, Plains, AgelessGuardian))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)

      manager.currentAction must bePriorityChoice.forPlayer(playerOne).withAvailableSpell(EnvironmentalSciences)
    }

    "offer the choice of a basic land" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Forest, 2)
        .setHand(playerOne, EnvironmentalSciences)
        .setLibrary(playerOne, Seq(Island, Plains, AgelessGuardian))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 2)
      manager.castSpell(playerOne, EnvironmentalSciences)
      manager.resolveNext()

      manager.currentAction must beAnInstanceOf[ResolveEffectChoice]
      manager.currentAction.asInstanceOf[ResolveEffectChoice].effectChoice.playerChoosing mustEqual playerOne
      manager.currentAction.asInstanceOf[ResolveEffectChoice].effectChoice must beAnInstanceOf[SearchChoice]
      manager.currentAction.asInstanceOf[ResolveEffectChoice].effectChoice.asInstanceOf[SearchChoice].zone mustEqual Zone.Library(playerOne)
      manager.currentAction.asInstanceOf[ResolveEffectChoice].effectChoice.asInstanceOf[SearchChoice].possibleChoices mustEqual manager.getCards(Island, Plains).map(_.objectId)
    }

    "put the chosen land into its controller's hand" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Forest, 2)
        .setHand(playerOne, EnvironmentalSciences)
        .setLibrary(playerOne, Seq(Island, Plains, AgelessGuardian))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 2)
      manager.castSpell(playerOne, EnvironmentalSciences)
      manager.resolveNext()
      manager.chooseCard(playerOne, Island)

      playerOne.hand(manager.currentGameState) must contain(exactly(beCardObject(Island)))
    }

    "shuffle its controller's library" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Forest, 2)
        .setHand(playerOne, EnvironmentalSciences)
        .setLibrary(playerOne, Seq(Island, Plains, AgelessGuardian))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 2)
      manager.castSpell(playerOne, EnvironmentalSciences)
      manager.resolveNext()
      manager.chooseCard(playerOne, Island)

      val finalState = manager.currentGameState
      playerOne.library(finalState).map(_.objectId) must not(contain(anyOf(playerOne.library(initialState).map(_.objectId): _*)))
    }

    "gain its controller two life" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Forest, 2)
        .setHand(playerOne, EnvironmentalSciences)
        .setLibrary(playerOne, Seq(Island, Plains, AgelessGuardian))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 2)
      manager.castSpell(playerOne, EnvironmentalSciences)
      manager.resolveNext()
      manager.chooseCard(playerOne, Island)

      manager.currentGameState.gameObjectState.lifeTotals(playerOne) mustEqual 22
    }

    "not allow choosing a creature" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Forest, 2)
        .setHand(playerOne, EnvironmentalSciences)
        .setLibrary(playerOne, Seq(Island, Plains, AgelessGuardian))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 2)
      manager.castSpell(playerOne, EnvironmentalSciences)
      manager.resolveNext()

      val stateBeforeChoice = manager.currentGameState
      manager.chooseCard(playerOne, AgelessGuardian)

      manager.currentGameState mustEqual stateBeforeChoice
    }

    "have correct oracle text" in {
      EnvironmentalSciences.text mustEqual "Search your library for a basic land card, reveal it, put it into your hand, then shuffle. You gain 2 life."
    }

    // TODO: not allow a non-basic land?
  }

}
