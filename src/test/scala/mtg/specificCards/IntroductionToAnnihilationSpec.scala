package mtg.specificCards

import mtg.SpecWithGameStateManager
import mtg.core.zones.Zone
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.sets.alpha.cards.{Forest, Plains, SavannahLions}
import mtg.sets.kaldheim.cards.GrizzledOutrider
import mtg.sets.strixhaven.cards.{IntroductionToAnnihilation, SpinedKarok}

class IntroductionToAnnihilationSpec extends SpecWithGameStateManager {
  "Introduction to Annihilation" should {
    "target any non-land permanent" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, IntroductionToAnnihilation, GrizzledOutrider)
        .setBattlefield(playerOne, Plains, Plains, Plains, Plains, Plains, SavannahLions)
        .setBattlefield(playerTwo, Forest, Forest, Forest, SpinedKarok)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 5)
      manager.castSpell(playerOne, IntroductionToAnnihilation)

      // TODO: check non-creature permanents
      implicit val gameObjectState = manager.gameState.gameObjectState
      manager.currentChoice must beSome(beTargetChoice.forPlayer(playerOne).withAvailableTargets(SavannahLions, SpinedKarok))
    }

    "exile its target" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, IntroductionToAnnihilation, GrizzledOutrider)
        .setBattlefield(playerOne, Plains, Plains, Plains, Plains, Plains, SavannahLions)
        .setBattlefield(playerTwo, Forest, Forest, Forest, SpinedKarok)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 5)
      manager.castSpell(playerOne, IntroductionToAnnihilation)
      manager.chooseCard(playerOne, SpinedKarok)
      manager.resolveNext()

      Zone.Battlefield(manager.gameState) must not(contain(beCardObject(SpinedKarok)))
      Zone.Exile(manager.gameState) must contain(beCardObject(SpinedKarok))
    }

    "draw its target's controller a card" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, IntroductionToAnnihilation, GrizzledOutrider)
        .setBattlefield(playerOne, Plains, Plains, Plains, Plains, Plains, SavannahLions)
        .setBattlefield(playerTwo, Forest, Forest, Forest, SpinedKarok)
        .setLibrary(playerTwo, Forest)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 5)
      manager.castSpell(playerOne, IntroductionToAnnihilation)
      manager.chooseCard(playerOne, SpinedKarok)

      val gameStateBeforeResolution = manager.gameState
      manager.resolveNext()
      val gameStateAfterResolution = manager.gameState

      playerTwo.hand(gameStateAfterResolution).size mustEqual playerTwo.hand(gameStateBeforeResolution).size + 1
    }

    "have correct oracle text" in {
      IntroductionToAnnihilation.text mustEqual "Exile target nonland permanent. Its controller draws a card."
    }
  }
}
