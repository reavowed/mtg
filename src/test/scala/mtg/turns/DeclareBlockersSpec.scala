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
        .setHand(playerOne, Seq(AgelessGuardian))
        .setBattlefield(playerOne, Seq(Plains, Plains))
        .setHand(playerTwo, Seq(SpinedKarok))
        .setBattlefield(playerTwo, Seq(Forest, Forest, Forest))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      // Cast attacker
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateFirstAbility(playerOne, Plains)
      manager.activateFirstAbility(playerOne, Plains)
      manager.castSpell(playerOne, AgelessGuardian)

      // Cast blocker
      manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
      manager.activateFirstAbility(playerTwo, Forest)
      manager.activateFirstAbility(playerTwo, Forest)
      manager.activateFirstAbility(playerTwo, Forest)
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
