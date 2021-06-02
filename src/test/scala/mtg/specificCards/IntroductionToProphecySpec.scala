package mtg.specificCards

import mtg.SpecWithGameStateManager
import mtg.data.cards.alpha.SavannahLions
import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.strixhaven.{IntroductionToAnnihilation, IntroductionToProphecy, SpinedKarok}
import mtg.data.cards.{Forest, Island, Plains, Swamp}
import mtg.effects.oneshot.actions.ScryChoice
import mtg.game.Zone
import mtg.game.turns.StartNextTurnAction
import mtg.game.turns.TurnPhase.PrecombatMainPhase

class IntroductionToProphecySpec extends SpecWithGameStateManager {
  "Introduction to Prophecy" should {
    "scry the top two" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(IntroductionToProphecy))
        .setBattlefield(playerOne, Plains, 3)
        .setLibrary(playerOne, Seq(Plains, Island, Swamp))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, IntroductionToProphecy)
      manager.resolveNext()

      val topTwoCards = playerOne.library(manager.currentGameState).take(2).map(_.objectId)
      manager.currentAction must beEffectChoice[ScryChoice](beEqualTo(topTwoCards) ^^ ((_: ScryChoice).cardsBeingScryed))
    }

    "reorder the top two" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(IntroductionToProphecy))
        .setBattlefield(playerOne, Plains, 3)
        .setLibrary(playerOne, Seq(Plains, Island, Swamp))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      val plainsId = playerOne.library(manager.currentGameState)(0).objectId
      val islandId = playerOne.library(manager.currentGameState)(1).objectId

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, IntroductionToProphecy)
      manager.resolveNext()
      manager.handleDecision(s"$islandId $plainsId|", playerOne)

      playerOne.hand(manager.currentGameState) must contain(exactly(beCardObject(Island)))
      playerOne.library(manager.currentGameState) must contain(exactly(beCardObject(Plains), beCardObject(Swamp)).inOrder)
    }

    "put one on bottom" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(IntroductionToProphecy))
        .setBattlefield(playerOne, Plains, 3)
        .setLibrary(playerOne, Seq(Plains, Island, Swamp))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      val plainsId = playerOne.library(manager.currentGameState)(0).objectId
      val islandId = playerOne.library(manager.currentGameState)(1).objectId

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, IntroductionToProphecy)
      manager.resolveNext()
      manager.handleDecision(s"$islandId|$plainsId", playerOne)

      playerOne.hand(manager.currentGameState) must contain(exactly(beCardObject(Island)))
      playerOne.library(manager.currentGameState) must contain(exactly(beCardObject(Swamp), beCardObject(Plains)).inOrder)
    }

    "put both on bottom" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(IntroductionToProphecy))
        .setBattlefield(playerOne, Plains, 3)
        .setLibrary(playerOne, Seq(Plains, Island, Swamp))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      val plainsId = playerOne.library(manager.currentGameState)(0).objectId
      val islandId = playerOne.library(manager.currentGameState)(1).objectId

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, IntroductionToProphecy)
      manager.resolveNext()
      manager.handleDecision(s"|$islandId $plainsId", playerOne)

      playerOne.hand(manager.currentGameState) must contain(exactly(beCardObject(Swamp)))
      playerOne.library(manager.currentGameState) must contain(exactly(beCardObject(Island), beCardObject(Plains)).inOrder)
    }

    "scry one if only one card in library" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(IntroductionToProphecy))
        .setBattlefield(playerOne, Plains, 3)
        .setLibrary(playerOne, Seq(Swamp))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      val swampId = playerOne.library(manager.currentGameState)(0).objectId

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, IntroductionToProphecy)
      manager.resolveNext()
      manager.handleDecision(s"|$swampId", playerOne)

      playerOne.hand(manager.currentGameState) must contain(exactly(beCardObject(Swamp)))
    }

    "have correct oracle text" in {
      IntroductionToProphecy.text mustEqual "Scry 2, then draw a card."
    }
  }
}
