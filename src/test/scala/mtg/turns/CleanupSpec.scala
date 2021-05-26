package mtg.turns

import mtg.SpecWithGameStateManager
import mtg.data.cards.{Forest, Plains}
import mtg.data.cards.strixhaven.{AgelessGuardian, SpinedKarok}
import mtg.game.Zone
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.{StartNextTurnAction, TurnStep}

class CleanupSpec extends SpecWithGameStateManager {
  "cleanup step" should {
    "wear off damage" in {
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

      manager.passUntilStep(TurnStep.EndStep)
      manager.getCard(Zone.Battlefield, AgelessGuardian).markedDamage mustEqual 2
      manager.getCard(Zone.Battlefield, SpinedKarok).markedDamage mustEqual 1

      manager.passUntilTurn(4)
      manager.getCard(Zone.Battlefield, AgelessGuardian).markedDamage mustEqual 0
      manager.getCard(Zone.Battlefield, SpinedKarok).markedDamage mustEqual 0
    }
  }

}
