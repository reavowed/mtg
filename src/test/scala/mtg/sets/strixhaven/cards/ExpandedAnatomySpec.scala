package mtg.sets.strixhaven.cards

import mtg.SpecWithGameStateManager
import mtg.abilities.AbilityDefinition
import mtg.abilities.keyword.Vigilance
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.parts.Counter
import mtg.sets.alpha.cards.{Forest, Plains, SavannahLions}
import mtg.sets.kaldheim.cards.GrizzledOutrider

class ExpandedAnatomySpec extends SpecWithGameStateManager {
  "Expanded Anatomy" should {
    "be castable with target" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Plains, Plains, Plains, SavannahLions)
        .setHand(playerOne, ExpandedAnatomy)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne).withAvailableSpell(ExpandedAnatomy))
    }

    "allow targeting any creature on the battlefield" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, ExpandedAnatomy, GrizzledOutrider)
        .setBattlefield(playerOne, Plains, Plains, Plains, SavannahLions)
        .setBattlefield(playerTwo, Forest, Forest, Forest, SpinedKarok)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, ExpandedAnatomy)

      implicit val gameObjectState = manager.gameState.gameObjectState
      manager.currentChoice must beSome(beTargetChoice.forPlayer(playerOne).withAvailableTargets(SavannahLions, SpinedKarok))
    }

    "put counters on targeted creature" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, ExpandedAnatomy)
        .setBattlefield(playerOne, Plains, Plains, Plains, SavannahLions)
        .setBattlefield(playerTwo, Forest, Forest, Forest, SpinedKarok)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, ExpandedAnatomy)
      manager.chooseCard(playerOne, SavannahLions)
      manager.resolveNext()

      manager.getState(manager.getCard(SavannahLions)).gameObject.counters(Counter.PlusOnePlusOne) mustEqual 2
      manager.getState(manager.getCard(SavannahLions)).characteristics.power must beSome(4)
      manager.getState(manager.getCard(SavannahLions)).characteristics.toughness must beSome(3)
    }

    "grants the creature vigilance" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, ExpandedAnatomy)
        .setBattlefield(playerOne, Plains, Plains, Plains, SavannahLions)
        .setBattlefield(playerTwo, Forest, Forest, Forest, SpinedKarok)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 3)
      manager.castSpell(playerOne, ExpandedAnatomy)
      manager.chooseCard(playerOne, SavannahLions)
      manager.resolveNext()

      manager.getState(manager.getCard(SavannahLions)).characteristics.abilities must contain[AbilityDefinition](Vigilance)
    }

    "not grant the creature vigilance after the current turn" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, ExpandedAnatomy)
        .setBattlefield(playerOne, Plains, Plains, Plains, SavannahLions)
        .setBattlefield(playerTwo, Forest, Forest, Forest, SpinedKarok)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

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
