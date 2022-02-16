package mtg.stateBasedActions

import mtg.SpecWithGameStateManager
import mtg.core.zones.Zone
import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.strixhaven.AgelessGuardian
import mtg.data.cards.{Forest, Plains}
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.TurnStep

class LethalDamageStateBasedActionSpec extends SpecWithGameStateManager {
  "SBA for lethal damage" should {
    "put a creature dealt lethal combat damage in the graveyard" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, GrizzledOutrider)
        .setBattlefield(playerOne, Forest, 5)
        .setHand(playerTwo, AgelessGuardian)
        .setBattlefield(playerTwo, Plains, 2)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

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

      Zone.Battlefield(manager.gameState) must not(contain(beCardObject(AgelessGuardian)))
      Zone.Graveyard(playerOne)(manager.gameState) must beEmpty
      Zone.Graveyard(playerTwo)(manager.gameState) must contain(exactly(beCardObject(AgelessGuardian)))
    }
  }
}
