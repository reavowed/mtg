package mtg.specificCards

import mtg.SpecWithGameStateManager
import mtg.data.sets.alpha.cards.{Island, Plains, Swamp}
import mtg.data.sets.strixhaven.cards.IntroductionToProphecy
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.instructions.verbs.ScryChoice

class IntroductionToProphecySpec extends SpecWithGameStateManager {
  "Introduction to Prophecy" should {
    "scry the top two" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, IntroductionToProphecy)
        .setBattlefield(playerOne, Plains, 3)
        .setLibrary(playerOne, Plains, Island, Swamp)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, IntroductionToProphecy)
      manager.resolveNext()

      val topTwoCards = playerOne.library(manager.gameState).take(2).map(_.objectId)
      manager.currentChoice must beSome(beInstructionChoice[ScryChoice](beEqualTo(topTwoCards) ^^ ((_: ScryChoice).cardsBeingScryed)))
    }

    "reorder the top two" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, IntroductionToProphecy)
        .setBattlefield(playerOne, Plains, 3)
        .setLibrary(playerOne, Plains, Island, Swamp)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      val plainsId = playerOne.library(manager.gameState)(0).objectId
      val islandId = playerOne.library(manager.gameState)(1).objectId

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, IntroductionToProphecy)
      manager.resolveNext()
      manager.handleDecision(s"$islandId $plainsId|", playerOne)

      playerOne.hand(manager.gameState) must contain(exactly(beCardObject(Island)))
      playerOne.library(manager.gameState) must contain(exactly(beCardObject(Plains), beCardObject(Swamp)).inOrder)
    }

    "put one on bottom" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, IntroductionToProphecy)
        .setBattlefield(playerOne, Plains, 3)
        .setLibrary(playerOne, Plains, Island, Swamp)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      val plainsId = playerOne.library(manager.gameState)(0).objectId
      val islandId = playerOne.library(manager.gameState)(1).objectId

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, IntroductionToProphecy)
      manager.resolveNext()
      manager.handleDecision(s"$islandId|$plainsId", playerOne)

      playerOne.hand(manager.gameState) must contain(exactly(beCardObject(Island)))
      playerOne.library(manager.gameState) must contain(exactly(beCardObject(Swamp), beCardObject(Plains)).inOrder)
    }

    "put both on bottom" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, IntroductionToProphecy)
        .setBattlefield(playerOne, Plains, 3)
        .setLibrary(playerOne, Plains, Island, Swamp)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      val plainsId = playerOne.library(manager.gameState)(0).objectId
      val islandId = playerOne.library(manager.gameState)(1).objectId

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, IntroductionToProphecy)
      manager.resolveNext()
      manager.handleDecision(s"|$islandId $plainsId", playerOne)

      playerOne.hand(manager.gameState) must contain(exactly(beCardObject(Swamp)))
      playerOne.library(manager.gameState) must contain(exactly(beCardObject(Island), beCardObject(Plains)).inOrder)
    }

    "scry one if only one card in library" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, IntroductionToProphecy)
        .setBattlefield(playerOne, Plains, 3)
        .setLibrary(playerOne, Swamp)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      val swampId = playerOne.library(manager.gameState)(0).objectId

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, IntroductionToProphecy)
      manager.resolveNext()
      manager.handleDecision(s"|$swampId", playerOne)

      playerOne.hand(manager.gameState) must contain(exactly(beCardObject(Swamp)))
    }

    "have correct oracle text" in {
      IntroductionToProphecy.text mustEqual "Scry 2, then draw a card."
    }
  }
}
