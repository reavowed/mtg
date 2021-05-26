package mtg.turns

import mtg.SpecWithGameStateManager
import mtg.data.cards.strixhaven.{AgelessGuardian, SpinedKarok}
import mtg.data.cards.{Forest, Plains}
import mtg.game.Zone
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.turnBasedActions.DeclareBlockersChoice
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
  }
}
