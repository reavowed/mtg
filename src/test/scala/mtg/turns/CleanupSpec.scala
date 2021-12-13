package mtg.turns

import mtg.SpecWithGameStateManager
import mtg.data.cards.strixhaven.{AgelessGuardian, SpinedKarok}
import mtg.data.cards.{Forest, Plains}
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.TurnStep

class CleanupSpec extends SpecWithGameStateManager {
  "cleanup step" should {
    "wear off damage" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, AgelessGuardian)
        .setBattlefield(playerOne, Plains, 2)
        .setHand(playerTwo, SpinedKarok)
        .setBattlefield(playerTwo, Forest, 3)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

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
      manager.getPermanent(AgelessGuardian).markedDamage mustEqual 2
      manager.getPermanent(SpinedKarok).markedDamage mustEqual 1

      manager.passUntilTurn(4)
      manager.getPermanent(AgelessGuardian).markedDamage mustEqual 0
      manager.getPermanent(SpinedKarok).markedDamage mustEqual 0
    }
  }

}
