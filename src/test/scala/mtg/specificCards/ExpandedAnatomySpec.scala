package mtg.specificCards

import mtg.SpecWithGameStateManager
import mtg.abilities.AbilityDefinition
import mtg.abilities.keyword.Vigilance
import mtg.data.cards.alpha.SavannahLions
import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.strixhaven.{AgelessGuardian, EnvironmentalSciences, ExpandedAnatomy, SpinedKarok}
import mtg.data.cards.{Forest, Island, Plains}
import mtg.effects.oneshot.basic.SearchChoice
import mtg.game.Zone
import mtg.game.actions.ResolveEffectChoice
import mtg.game.turns.StartNextTurnAction
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.parts.counters.PlusOnePlusOneCounter

class ExpandedAnatomySpec extends SpecWithGameStateManager {
  "Expanded Anatomy" should {
    "be castable with target" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Seq(Plains, Plains, Plains, SavannahLions))
        .setHand(playerOne, Seq(ExpandedAnatomy))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)

      manager.currentAction must bePriorityChoice.forPlayer(playerOne).withAvailableSpell(ExpandedAnatomy)
    }

    "allow targeting any creature on the battlefield" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(ExpandedAnatomy, GrizzledOutrider))
        .setBattlefield(playerOne, Seq(Plains, Plains, Plains, SavannahLions))
        .setBattlefield(playerTwo, Seq(Forest, Forest, Forest, SpinedKarok))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, ExpandedAnatomy)

      implicit val gameObjectState = manager.currentGameState.gameObjectState
      manager.currentAction must beTargetChoice.forPlayer(playerOne).withAvailableTargets(SavannahLions, SpinedKarok)
    }

    "put counters on targeted creature" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(ExpandedAnatomy))
        .setBattlefield(playerOne, Seq(Plains, Plains, Plains, SavannahLions))
        .setBattlefield(playerTwo, Seq(Forest, Forest, Forest, SpinedKarok))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, ExpandedAnatomy)
      manager.chooseCard(playerOne, SavannahLions)
      manager.resolveNext()

      manager.getState(manager.getCard(SavannahLions)).gameObject.counters(PlusOnePlusOneCounter) mustEqual 2
      manager.getState(manager.getCard(SavannahLions)).characteristics.power must beSome(4)
      manager.getState(manager.getCard(SavannahLions)).characteristics.toughness must beSome(3)
    }

    "grants the creature vigilance" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(ExpandedAnatomy))
        .setBattlefield(playerOne, Seq(Plains, Plains, Plains, SavannahLions))
        .setBattlefield(playerTwo, Seq(Forest, Forest, Forest, SpinedKarok))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, ExpandedAnatomy)
      manager.chooseCard(playerOne, SavannahLions)
      manager.resolveNext()

      manager.getState(manager.getCard(SavannahLions)).characteristics.abilities must contain[AbilityDefinition](Vigilance)
    }

    "not grant the creature vigilance after the current turn" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(ExpandedAnatomy))
        .setBattlefield(playerOne, Seq(Plains, Plains, Plains, SavannahLions))
        .setBattlefield(playerTwo, Seq(Forest, Forest, Forest, SpinedKarok))
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, ExpandedAnatomy)
      manager.chooseCard(playerOne, SavannahLions)
      manager.resolveNext()
      manager.passUntilTurn(2)

      manager.getState(manager.getCard(SavannahLions)).characteristics.abilities must not(contain[AbilityDefinition](Vigilance))
    }

    "have correct oracle text" in {
      ExpandedAnatomy.text mustEqual "Put two +1/+1 counters on target creature. It gains vigilance until end of turn."
    }
  }
}
