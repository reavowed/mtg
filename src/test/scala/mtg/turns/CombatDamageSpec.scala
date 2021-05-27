package mtg.turns

import mtg.SpecWithGameStateManager
import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.{Forest, Plains}
import mtg.data.cards.strixhaven.{AgelessGuardian, SpinedKarok}
import mtg.game.Zone
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.{StartNextTurnAction, TurnStep}

class CombatDamageSpec extends SpecWithGameStateManager {
    // TODO: be skipped if no attackers
  "an unblocked creature should deal combat damage to defending player" in {
    val initialState = gameObjectStateWithInitialLibrariesAndHands
      .setHand(playerOne, Seq(AgelessGuardian))
      .setBattlefield(playerOne, Seq(Plains, Plains))

    val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
    manager.passUntilPhase(PrecombatMainPhase)

    // Tap mana and cast creature
    manager.activateFirstAbility(playerOne, Plains)
    manager.activateFirstAbility(playerOne, Plains)
    manager.castSpell(playerOne, AgelessGuardian)

    manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)
    manager.attackWith(playerOne, AgelessGuardian)
    manager.passUntilStep(TurnStep.CombatDamageStep)

    manager.currentGameState.gameObjectState.lifeTotals(playerTwo) mustEqual 19
  }

  "a blocked creature and its blocker should deal damage to each other" in {
    val initialState = gameObjectStateWithInitialLibrariesAndHands
      .setHand(playerOne, AgelessGuardian)
      .setBattlefield(playerOne, Plains, 2)
      .setHand(playerTwo, SpinedKarok)
      .setBattlefield(playerTwo, Forest, 3)

    val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

    // Cast attacker
    manager.passUntilPhase(PrecombatMainPhase)
    manager.activateAbilities(playerOne, Plains, 2)
    manager.castSpell(playerOne, AgelessGuardian)

    // Cast blocker
    manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
    manager.activateAbilities(playerTwo, Forest, 3)
    manager.castSpell(playerTwo, SpinedKarok)

    manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)
    manager.attackWith(playerOne, AgelessGuardian)
    manager.passUntilTurnAndStep(3, TurnStep.DeclareBlockersStep)
    manager.block(playerTwo, SpinedKarok, AgelessGuardian)
    manager.passUntilStep(TurnStep.CombatDamageStep)

    manager.currentGameState.gameObjectState.lifeTotals(playerOne) mustEqual 20
    manager.currentGameState.gameObjectState.lifeTotals(playerTwo) mustEqual 20
    manager.getCard(Zone.Battlefield, AgelessGuardian).markedDamage mustEqual 2
    manager.getCard(Zone.Battlefield, SpinedKarok).markedDamage mustEqual 1
  }

  "a blocked creature without trample should not deal excess damage to player" in {
    val initialState = gameObjectStateWithInitialLibrariesAndHands
      .setHand(playerOne, GrizzledOutrider)
      .setBattlefield(playerOne, Forest, 5)
      .setHand(playerTwo, AgelessGuardian)
      .setBattlefield(playerTwo, Plains, 2)

    val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

    // Cast attacker
    manager.passUntilPhase(PrecombatMainPhase)
    manager.activateAbilities(playerOne, Forest, 5)
    manager.castSpell(playerOne, GrizzledOutrider)

    // Cast blocker
    manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
    manager.activateAbilities(playerTwo, Plains, 2)
    manager.castSpell(playerTwo, AgelessGuardian)

    manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)
    manager.attackWith(playerOne, GrizzledOutrider)
    manager.passUntilTurnAndStep(3, TurnStep.DeclareBlockersStep)
    manager.block(playerTwo, AgelessGuardian, GrizzledOutrider)
    manager.passUntilStep(TurnStep.CombatDamageStep)

    manager.currentGameState.gameObjectState.lifeTotals(playerOne) mustEqual 20
    manager.currentGameState.gameObjectState.lifeTotals(playerTwo) mustEqual 20
  }
}
