package mtg.turns

import mtg.SpecWithGameStateManager
import mtg.data.cards.Plains
import mtg.data.cards.strixhaven.AgelessGuardian
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.{StartNextTurnAction, TurnStep}

class CombatDamageSpec extends SpecWithGameStateManager {
  "combat damage" should {
    "deal damage from an unblocked creature to defending player" in {
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
  }
}
