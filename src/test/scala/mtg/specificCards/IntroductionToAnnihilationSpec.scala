package mtg.specificCards

import mtg.SpecWithGameStateManager
import mtg.data.cards.alpha.SavannahLions
import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.strixhaven.{IntroductionToAnnihilation, SpinedKarok}
import mtg.data.cards.{Forest, Plains}
import mtg.game.Zone
import mtg.game.turns.TurnPhase.PrecombatMainPhase

class IntroductionToAnnihilationSpec extends SpecWithGameStateManager {
  "Introduction to Annihilation" should {
    "target any non-land permanent" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(IntroductionToAnnihilation, GrizzledOutrider))
        .setBattlefield(playerOne, Seq(Plains, Plains, Plains, Plains, Plains, SavannahLions))
        .setBattlefield(playerTwo, Seq(Forest, Forest, Forest, SpinedKarok))
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
        .setHand(playerOne, Seq(IntroductionToAnnihilation, GrizzledOutrider))
        .setBattlefield(playerOne, Seq(Plains, Plains, Plains, Plains, Plains, SavannahLions))
        .setBattlefield(playerTwo, Seq(Forest, Forest, Forest, SpinedKarok))
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
        .setHand(playerOne, Seq(IntroductionToAnnihilation, GrizzledOutrider))
        .setBattlefield(playerOne, Seq(Plains, Plains, Plains, Plains, Plains, SavannahLions))
        .setBattlefield(playerTwo, Seq(Forest, Forest, Forest, SpinedKarok))
        .setLibrary(playerTwo, Seq(Forest))
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
