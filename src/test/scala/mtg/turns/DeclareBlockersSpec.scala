package mtg.turns

import mtg.SpecWithGameStateManager
import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.strixhaven.{AgelessGuardian, SpinedKarok}
import mtg.data.cards.{Forest, Plains}
import mtg.game.Zone
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.turnBasedActions.{DeclareBlockersChoice, OrderBlockersChoice}
import mtg.game.turns.{StartNextTurnAction, TurnStep}

class DeclareBlockersSpec extends SpecWithGameStateManager {
  "declare blockers step" should {
    // TODO: be skipped if no attackers

    "offer choice to block with an untapped creature" in {
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

      manager.currentAction must beAnInstanceOf[DeclareBlockersChoice]
      manager.currentAction.asInstanceOf[DeclareBlockersChoice].possibleBlockers must contain(exactly(
        manager.getCard(Zone.Battlefield, SpinedKarok).objectId
      ))
      manager.currentAction.asInstanceOf[DeclareBlockersChoice].attackers must contain(exactly(
        manager.getCard(Zone.Battlefield, AgelessGuardian).objectId
      ))
    }

    "require ordering if a single creature blocks an attacker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, AgelessGuardian)
        .setBattlefield(playerOne, Plains, 2)
        .setHand(playerTwo, Seq(SpinedKarok))
        .setBattlefield(playerTwo, Forest, 3)
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      // Cast attacker
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 2)
      manager.castSpell(playerOne, AgelessGuardian)

      // Cast blockers
      manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
      manager.activateAbilities(playerTwo, Forest, 3)
      manager.castSpell(playerTwo, SpinedKarok)

      // Attack and block
      manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, AgelessGuardian)
      manager.passUntilTurnAndStep(3, TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, SpinedKarok, AgelessGuardian)

      manager.currentAction must not(beAnInstanceOf[OrderBlockersChoice])
    }

    "require ordering if multiple creatures block the same attacker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, AgelessGuardian)
        .setBattlefield(playerOne, Plains, 2)
        .setHand(playerTwo, Seq(SpinedKarok, GrizzledOutrider))
        .setBattlefield(playerTwo, Forest, 8)
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      // Cast attacker
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 2)
      manager.castSpell(playerOne, AgelessGuardian)

      // Cast blockers
      manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
      manager.activateAbilities(playerTwo, Forest, 3)
      manager.castSpell(playerTwo, SpinedKarok)
      manager.passUntilStackEmpty()
      manager.activateAbilities(playerTwo, Forest, 5)
      manager.castSpell(playerTwo, GrizzledOutrider)

      // Attack and block
      manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, AgelessGuardian)
      manager.passUntilTurnAndStep(3, TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, (SpinedKarok, AgelessGuardian), (GrizzledOutrider, AgelessGuardian))

      manager.currentAction must beAnInstanceOf[OrderBlockersChoice]
      manager.currentAction.asInstanceOf[OrderBlockersChoice].playerToAct mustEqual playerOne
      manager.currentAction.asInstanceOf[OrderBlockersChoice].attacker mustEqual manager.getCard(Zone.Battlefield, AgelessGuardian).objectId
      manager.currentAction.asInstanceOf[OrderBlockersChoice].blockers must contain(exactly(
        manager.getCard(Zone.Battlefield, GrizzledOutrider).objectId,
        manager.getCard(Zone.Battlefield, SpinedKarok).objectId))
    }
  }
}