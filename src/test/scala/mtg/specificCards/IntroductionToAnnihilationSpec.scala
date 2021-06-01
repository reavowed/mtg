package mtg.specificCards

import mtg.SpecWithGameStateManager
import mtg.abilities.AbilityDefinition
import mtg.abilities.keyword.Vigilance
import mtg.data.cards.alpha.SavannahLions
import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.strixhaven.{AgelessGuardian, ExpandedAnatomy, IntroductionToAnnihilation, SpinedKarok}
import mtg.data.cards.{Forest, Plains}
import mtg.game.Zone
import mtg.game.turns.StartNextTurnAction
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.parts.counters.PlusOnePlusOneCounter

class IntroductionToAnnihilationSpec extends SpecWithGameStateManager {
  "Introduction to Annihilation" should {
    "target any non-land permanent" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(IntroductionToAnnihilation, GrizzledOutrider))
        .setBattlefield(playerOne, Seq(Plains, Plains, Plains, Plains, Plains, SavannahLions))
        .setBattlefield(playerTwo, Seq(Forest, Forest, Forest, SpinedKarok))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 5)
      manager.castSpell(playerOne, IntroductionToAnnihilation)

      // TODO: check non-creature permanents
      implicit val gameObjectState = manager.currentGameState.gameObjectState
      manager.currentAction must beTargetChoice.forPlayer(playerOne).withAvailableTargets(SavannahLions, SpinedKarok)
    }

    "exile its target" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(IntroductionToAnnihilation, GrizzledOutrider))
        .setBattlefield(playerOne, Seq(Plains, Plains, Plains, Plains, Plains, SavannahLions))
        .setBattlefield(playerTwo, Seq(Forest, Forest, Forest, SpinedKarok))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 5)
      manager.castSpell(playerOne, IntroductionToAnnihilation)
      manager.chooseCard(playerOne, SpinedKarok)
      manager.resolveNext()

      Zone.Battlefield(manager.currentGameState) must not(contain(beCardObject(SpinedKarok)))
      Zone.Exile(manager.currentGameState) must contain(beCardObject(SpinedKarok))
    }

    "draw its target's controller a card" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(IntroductionToAnnihilation, GrizzledOutrider))
        .setBattlefield(playerOne, Seq(Plains, Plains, Plains, Plains, Plains, SavannahLions))
        .setBattlefield(playerTwo, Seq(Forest, Forest, Forest, SpinedKarok))
        .setLibrary(playerTwo, Seq(Forest))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 5)
      manager.castSpell(playerOne, IntroductionToAnnihilation)
      manager.chooseCard(playerOne, SpinedKarok)

      val gameStateBeforeResolution = manager.currentGameState
      manager.resolveNext()
      val gameStateAfterResolution = manager.currentGameState

      playerTwo.hand(gameStateAfterResolution).size mustEqual playerTwo.hand(gameStateBeforeResolution).size + 1
    }

    "have correct oracle text" in {
      IntroductionToAnnihilation.text mustEqual "Exile target nonland permanent. Its controller draws a card."
    }
  }
}
